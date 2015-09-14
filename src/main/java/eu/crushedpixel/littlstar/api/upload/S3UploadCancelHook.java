package eu.crushedpixel.littlstar.api.upload;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.crushedpixel.littlstar.api.LittlstarApiClient;
import eu.crushedpixel.littlstar.api.LittlstarApiException;
import eu.crushedpixel.littlstar.api.data.EmptyData;
import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;
import eu.crushedpixel.littlstar.api.data.upload.CreateUpload;
import eu.crushedpixel.littlstar.api.upload.progress.S3UploadProgressListener;

import java.io.File;

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
 * A simple Hook which can be passed to
 * {@link eu.crushedpixel.littlstar.api.LittlstarApiClient#uploadFileToS3(File, CreateUpload.CreateUploadResponse, S3UploadProgressListener, S3UploadCancelHook)},
 * cancelling a running S3 Upload when desired
 */
public class S3UploadCancelHook {

    private LittlstarApiClient littlstarApiClient;
    private S3Uploader s3Uploader;
    private CreateUpload.CreateUploadResponse createUploadResponse;

    public void initialize(LittlstarApiClient littlstarApiClient, S3Uploader s3Uploader,
                           CreateUpload.CreateUploadResponse createUploadResponse) {
        this.littlstarApiClient = littlstarApiClient;
        this.s3Uploader = s3Uploader;
        this.createUploadResponse = createUploadResponse;
    }

    /**
     * Cancels the assigned S3 Upload, also notifying the Littlstar API that the Upload was canceled.
     * @return The API's response to the cancelFileUpload call, containing only the meta object
     * @throws com.mashape.unirest.http.exceptions.UnirestException If the http connection to the Littlstar API fails
     * @throws eu.crushedpixel.littlstar.api.LittlstarApiException If the Littlstar API returns an error code
     * when cancelling the Upload and <b>setThrowApiExceptions</b> is set to true
     */
    public ResponseWrapper<EmptyData> cancelS3Upload() throws UnirestException, LittlstarApiException {
        s3Uploader.cancelUpload();
        return littlstarApiClient.cancelFileUpload(createUploadResponse.getId());
    }

}
