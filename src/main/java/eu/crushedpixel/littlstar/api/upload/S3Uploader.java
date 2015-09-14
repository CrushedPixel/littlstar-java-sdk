package eu.crushedpixel.littlstar.api.upload;

import com.mashape.unirest.http.utils.ClientFactory;
import eu.crushedpixel.littlstar.api.data.upload.CreateUpload;
import eu.crushedpixel.littlstar.api.upload.progress.UploadProgressListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;

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
@AllArgsConstructor
public class S3Uploader {

    private File file;
    private String s3_bucket, s3_key, s3_policy, s3_signature;

    public S3Uploader(File file, CreateUpload.CreateUploadResponse createUploadResponse) {
        this(file, createUploadResponse.getS3_bucket(), createUploadResponse.getS3_key(),
                createUploadResponse.getS3_policy(), createUploadResponse.getS3_signature());
    }

    /**
     * Executes a multipart form upload to the S3 Bucket as described in
     * <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/HTTPPOSTExamples.html">the AWS Documentation</a>.
     * @param uploadProgressListener An UploadProgressListener which is called whenever
     *                               bytes are written to the outgoing connection. May be null.
     * @return the HTTP request's response
     * @throws IOException in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     */
    public HttpResponse uploadFileToS3(UploadProgressListener uploadProgressListener)
            throws IOException, ClientProtocolException {
        //unfortunately, we can't use Unirest to execute the call, because there is no support
        //for Progress listeners (yet). See https://github.com/Mashape/unirest-java/issues/26
        //nevertheless, we'll use Unirest methods whereever possible
        HttpClient httpClient = ClientFactory.getHttpClient();

        FileBody fileBody = new CountingFileBody(file, uploadProgressListener);

        HttpPost httpPost = new HttpPost(s3_bucket);

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("acl", "public-read")
                .addTextBody("AWSAccessKeyId", s3_key)
                .addTextBody("Policy", s3_policy)
                .addTextBody("Signature", s3_signature)
                .addPart("file", fileBody)
                .build();

        httpPost.setEntity(httpEntity);

        return httpClient.execute(httpPost);
    }

}
