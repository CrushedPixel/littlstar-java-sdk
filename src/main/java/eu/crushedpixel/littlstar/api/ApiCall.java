package eu.crushedpixel.littlstar.api;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import eu.crushedpixel.littlstar.api.data.*;
import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;
import eu.crushedpixel.littlstar.api.data.upload.CreateUpload;
import eu.crushedpixel.littlstar.api.data.upload.UpdateUpload;
import eu.crushedpixel.littlstar.api.data.upload.UploadData;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/*
 * Copyright 2015 Marius Metzger - http://crushedpixel.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

@Data
public class ApiCall<SEND extends ApiSend, RECEIVE> {

    public static final ApiCall<Register.RegisterSend, Login.LoginResponse> REGISTER
            = new ApiCall<Register.RegisterSend, Login.LoginResponse>(HttpMethod.POST, "/api/v1/register", Login.LoginResponse.class, false);

    public static final ApiCall<Login.LoginSend, Login.LoginResponse> LOGIN
            = new ApiCall<Login.LoginSend, Login.LoginResponse>(HttpMethod.POST, "/api/v1/login", Login.LoginResponse.class, false);

    public static final ApiCall<CreateUpload.CreateUploadSend, CreateUpload.CreateUploadResponse> CREATE_UPLOAD
            = new ApiCall<CreateUpload.CreateUploadSend, CreateUpload.CreateUploadResponse>(HttpMethod.POST, "/api/private/uploads", CreateUpload.CreateUploadResponse.class, true);

    public static final ApiCall<UpdateUpload.UpdateUploadSend, UploadData> UPDATE_UPLOAD
            = new ApiCall<UpdateUpload.UpdateUploadSend, UploadData>(HttpMethod.PUT, "/api/private/uploads/{id}", UploadData.class, true)
            .addRequiredRouteParam("id");

    public static final ApiCall<EmptyData, EmptyData> CANCEL_UPLOAD
            = new ApiCall<EmptyData, EmptyData>(HttpMethod.DELETE, "/api/private/uploads/{id}", EmptyData.class, true)
            .addRequiredRouteParam("id");

    public static final ApiCall<EmptyData, UploadData> COMPLETE_UPLOAD
            = new ApiCall<EmptyData, UploadData>(HttpMethod.POST, "/api/private/uploads/{id}/complete", UploadData.class, true)
            .addRequiredRouteParam("id");

    private final HttpMethod httpMethod;
    private final String apiCall;

    private Class<RECEIVE> responseClass;

    private final Set<String> routeParams = new HashSet<String>();

    /**
     * Whether or not this Api Call requires a user-specific Apikey to be executed
     */
    private final boolean requiresAuthentication;

    private ApiCall(HttpMethod httpMethod, String apiCall, Class<RECEIVE> responseClass, boolean requiresAuthentication) {
        this.httpMethod = httpMethod;
        this.apiCall = apiCall;
        this.responseClass = responseClass;
        this.requiresAuthentication = requiresAuthentication;
    }

    public ApiCall<SEND, RECEIVE> addRequiredRouteParam(String key) {
        routeParams.add(key);
        return this;
    }

    public ApiCall<SEND, RECEIVE> removeRequiredRouteParam(String key) {
        routeParams.remove(key);
        return this;
    }

    public Set<String> getRequiredRouteParams() {
        return new HashSet<String>(routeParams);
    }

    @SuppressWarnings("unchecked")
    public ResponseWrapper<RECEIVE> execute(LittlstarApiClient apiClient, SEND data, StringPair... parameters)
            throws UnirestException {

        HttpRequestWithBody request = new HttpRequestWithBody(httpMethod, apiClient.getTLD() + apiCall);

        for(String key : routeParams) {
            boolean found = false;
            for(StringPair param : parameters) {
                if(param.getKey().equals(key)) {
                    found = true;
                    request.routeParam(key, param.getValue());
                    break;
                }
            }
            if(!found) {
                throw new IllegalArgumentException("No value passed for required API call key "+key);
            }
        }

        //always include the Application Token in the header
        request.header("X-AppToken", apiClient.getApplicationToken());

        //if required, include the authenticated user's Apikey in the header
        if(requiresAuthentication) {
            request.header("X-Apikey", apiClient.getUserApiKey());
        }

        SendWrapper<SEND> sendWrapper = new SendWrapper<SEND>(data);

        //send the SendWrapper as JSON Payload
        request.header("content-type", "application/json")
                .body(sendWrapper.toJson());

        String json = request.asString().getBody();

        return LittlstarApiClient.getGson(responseClass).fromJson(json, ResponseWrapper.class);
    }

}
