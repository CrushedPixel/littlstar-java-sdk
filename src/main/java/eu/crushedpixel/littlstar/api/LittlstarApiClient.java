package eu.crushedpixel.littlstar.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.crushedpixel.littlstar.api.data.EmptyData;
import eu.crushedpixel.littlstar.api.data.Login;
import eu.crushedpixel.littlstar.api.data.Register;
import eu.crushedpixel.littlstar.api.data.StringPair;
import eu.crushedpixel.littlstar.api.data.receive.Errors;
import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;
import eu.crushedpixel.littlstar.api.data.upload.CreateUpload;
import eu.crushedpixel.littlstar.api.data.upload.MimeType;
import eu.crushedpixel.littlstar.api.data.upload.UpdateUpload;
import eu.crushedpixel.littlstar.api.data.upload.UploadData;
import eu.crushedpixel.littlstar.api.gson.ErrorsDeserializer;
import eu.crushedpixel.littlstar.api.gson.ResponseWrapperTypeAdapter;
import eu.crushedpixel.littlstar.api.gson.RubyDateDeserializer;
import eu.crushedpixel.littlstar.api.upload.S3Uploader;
import eu.crushedpixel.littlstar.api.upload.progress.UploadProgressListener;
import lombok.Getter;
import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.IOException;
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
     * Registers a new user on Littlstar,
     * setting this LittlstarApiClient's apikey to the user's apikey if successful
     * @param username The desired username
     * @param email The E-Mail address connected to the account
     * @param password The account password
     * @return The API's response, containing information about the user
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<Login.LoginResponse> register(String username, String email, String password)
            throws UnirestException, LittlstarApiException {
        Register.RegisterSend registerSend = new Register.RegisterSend(username, email, password);

        ResponseWrapper<Login.LoginResponse> responseWrapper =
                ApiCall.REGISTER.execute(this, registerSend);
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        if(responseWrapper.getData() != null) {
            this.apiKey = responseWrapper.getData().getApikey();
        }

        return responseWrapper;
    }

    /**
     * Executes a login call to the Littlstar API,
     * setting this LittlstarApiClient's apikey to the user's apikey if successful
     * @param login The user's E-Mail address or Username
     * @param password The user's password
     * @return The API's response, containing information about the user
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<Login.LoginResponse> login(String login, String password)
            throws UnirestException, LittlstarApiException {
        Login.LoginSend loginSend = new Login.LoginSend(login, password);

        ResponseWrapper<Login.LoginResponse> responseWrapper =
                ApiCall.LOGIN.execute(this, loginSend);
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        if(responseWrapper.getData() != null) {
            this.apiKey = responseWrapper.getData().getApikey();
        }

        return responseWrapper;
    }

    /**
     * Removes the LittlstarApiClient's current Apikey.<br>
     * Until another Apikey is set, API calls that require user authentication are not possible.
     */
    public void logout() {
        this.apiKey = null;
    }

    /**
     * Requests to start a File Upload to Littlstar.<br>
     * This API call requires the LittlstarApiClient to be authenticated with a user.
     * @param mimeType The File's Mime Type
     * @param fileName The File's name
     * @return The API's response, containing information about the file that is being uploaded
     *         as well as the S3 Bucket access data to upload the file to
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<CreateUpload.CreateUploadResponse> createFileUpload(MimeType mimeType, String fileName)
            throws UnirestException, LittlstarApiException {
        CreateUpload.CreateUploadSend createUploadSend = new CreateUpload.CreateUploadSend(fileName, mimeType);

        ResponseWrapper<CreateUpload.CreateUploadResponse> responseWrapper =
                ApiCall.CREATE_UPLOAD.execute(this, createUploadSend);
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        return responseWrapper;
    }

    /**
     * Uploads a File to the Amazon S3 Bucket specified in the
     * {@link eu.crushedpixel.littlstar.api.data.upload.CreateUpload.CreateUploadResponse}
     * returned from the createFileUpload() call, and notificates the Littlstar Api about the finished upload.
     * @param fileToUpload The File to upload
     * @param createUploadResponse The response of the createFileUpload() call
     * @param uploadProgressListener An UploadProgressListener which is called whenever
     *                               bytes are written to the outgoing connection. May be null.
     * @return The API's response, containing information about the file that was uploaded
     * @throws IOException in case of a problem or the connection was aborted while interacting with S3
     * @throws ClientProtocolException in case of an http protocol error while interacting with S3
     * @throws UnirestException If the http connection fails while interacting with the Littlstar API
     * @throws LittlstarApiException If the Littlstar API returns an error code
     * and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<UploadData> uploadFileToS3(File fileToUpload, CreateUpload.CreateUploadResponse createUploadResponse,
                               UploadProgressListener uploadProgressListener)
            throws IOException, ClientProtocolException, UnirestException, LittlstarApiException {

        S3Uploader s3Uploader = new S3Uploader(fileToUpload, createUploadResponse);
        s3Uploader.uploadFileToS3(uploadProgressListener);

        return completeFileUpload(createUploadResponse.getId());
    }

    /**
     * Updates a currently pending Upload's File information.<br>
     * The updates are incremental, which means not all values of updateData have to be set.<br>
     * This API call requires the LittlstarApiClient to be authenticated with a user.
     * @param uploadID The File Upload's ID
     * @param updateData The File information to update. Only the values that are set will be updated.
     * @return The API's response, containing information about the file that is being uploaded
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<UploadData> updateFileUpload(int uploadID, UpdateUpload.UpdateUploadSend updateData)
            throws UnirestException, LittlstarApiException {

        ResponseWrapper<UploadData> responseWrapper =
                ApiCall.UPDATE_UPLOAD.execute(this, updateData, new StringPair("id", String.valueOf(uploadID)));
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        return responseWrapper;
    }

    /**
     * Cancels a currently pending File Upload.<br>
     * This API call requires the LittlstarApiClient to be authenticated with a user.
     * @param uploadID The ID of the File Upload to be canceled
     * @return The API's response, containing only the meta object
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<EmptyData> cancelFileUpload(int uploadID)
            throws UnirestException, LittlstarApiException {

        ResponseWrapper<EmptyData> responseWrapper =
                ApiCall.CANCEL_UPLOAD.execute(this, new EmptyData(), new StringPair("id", String.valueOf(uploadID)));
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        return responseWrapper;
    }

    /**
     * Marks a currently pending File Upload as finished.<br>
     * This method has to be called after the upload to the S3 Bucket,
     * which was retreived with the createUpload call has been finished.<br>
     * After executing this call, the File Upload will be removed from the Littlstar API.<br>
     * From now on, the File is accessible by the Slug contained in the returned UploadData object.<br>
     * This API call requires the LittlstarApiClient to be authenticated with a user.
     * @param uploadID The ID of the File Upload to be marked as finished
     * @return The API's response, containing information about the uploaded file
     * @throws UnirestException If the http connection fails
     * @throws LittlstarApiException If the API returns an error code and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<UploadData> completeFileUpload(int uploadID)
            throws UnirestException, LittlstarApiException {

        ResponseWrapper<UploadData> responseWrapper =
                ApiCall.COMPLETE_UPLOAD.execute(this, new EmptyData(), new StringPair("id", String.valueOf(uploadID)));
        RESPONSE_VALIDATOR.validateResponse(responseWrapper, throwApiExceptions);

        return responseWrapper;
    }
}
