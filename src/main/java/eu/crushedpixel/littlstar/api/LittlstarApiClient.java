package eu.crushedpixel.littlstar.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.crushedpixel.littlstar.api.data.Login;
import eu.crushedpixel.littlstar.api.data.receive.Errors;
import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;
import eu.crushedpixel.littlstar.api.gson.ErrorsDeserializer;
import eu.crushedpixel.littlstar.api.gson.ResponseWrapperTypeAdapter;
import eu.crushedpixel.littlstar.api.gson.RubyDateDeserializer;
import lombok.Getter;

import java.util.Date;

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

/**
 * An interface to interact with the Littlstar Web API.<br>
 * All API calls can and should only be made using an instance of LittlstarApiClient.
 */
public class LittlstarApiClient {

    private static final String LIVE_TLD = "https://littlstar.com";
    private static final String STAGING_TLD = "https://staging.littlstar.com";

    public static final Gson GSON = getGson(null);

    public static <T> Gson getGson(Class<T> wrapped) {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(Date.class, new RubyDateDeserializer())
                .registerTypeAdapter(Errors.class, new ErrorsDeserializer());

        if(wrapped != null) {
            builder.registerTypeAdapter(ResponseWrapper.class, new ResponseWrapperTypeAdapter<T>(wrapped));
        }

        return builder.create();
    }

    private static final ResponseValidator RESPONSE_VALIDATOR = new ResponseValidator();

    /**
     * The Application Token which is required to make any API call
     */
    @Getter
    private final String applicationToken;

    private String apiKey;

    /**
     * @return The Apikey which identifies an authenticated user (null if no user logged in)
     */
    public String getUserApiKey() {
        return apiKey;
    }

    private boolean liveEnvironment = true;

    private boolean throwApiExceptions = true;

    public LittlstarApiClient(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    /**
     * Manually set the Apikey to be used to identify a user when making API calls
     * @param apiKey The Apikey to use
     */
    public LittlstarApiClient setUserApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Whether or not to access the live API.<br>
     * @param liveEnvironment If set to true, all API calls will be made to {@value #LIVE_TLD},
     *                        otherwise all API calls will be made to {@value #STAGING_TLD}
     */
    public LittlstarApiClient setUseLiveEnvironment(boolean liveEnvironment) {
        this.liveEnvironment = liveEnvironment;
        return this;
    }

    /**
     * Whether or not to throw a LittlstarApiException when an API call doesn't return a response code of 200
     */
    public LittlstarApiClient setThrowApiExceptions(boolean throwApiExceptions) {
        this.throwApiExceptions = throwApiExceptions;
        return this;
    }

    public String getTLD() {
        return liveEnvironment ? LIVE_TLD : STAGING_TLD;
    }

    /**
     * Logs in the LittlstarApiClient to allow it to execute API calls on a user's behalf
     * @param login The user's E-Mail address or Username
     * @param password The user's password
     * @return Whether or not the login was successful
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the Api returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public boolean login(String login, String password) throws UnirestException, LittlstarApiException {
        Login.LoginSend loginSend = new Login.LoginSend(login, password);

        ResponseWrapper<Login.LoginResponse> responseWrapper = ApiCall.LOGIN.execute(this, loginSend);
        if(!RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions)) return false;

        this.apiKey = responseWrapper.getData().getApikey();
        return true;
    }

    /**
     * Removes the LittlstarApiClient's current Apikey.<br>
     * Until another Apikey is set, API calls that require user authentication are not possible.
     * @return Whether or not the logout was successful (always <code>true</code>)
     */
    public boolean logout() {
        this.apiKey = null;
        return true;
    }

    //TODO: Wrap the other API calls into user-friendly methods similar to login()
}
