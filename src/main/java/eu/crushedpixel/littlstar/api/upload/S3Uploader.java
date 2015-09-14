package eu.crushedpixel.littlstar.api.upload;

import eu.crushedpixel.littlstar.api.data.upload.CreateUpload;
import eu.crushedpixel.littlstar.api.upload.progress.S3UpdateProgressEvent;
import eu.crushedpixel.littlstar.api.upload.progress.S3UploadProgressListener;
import lombok.Data;
import org.apache.http.client.ClientProtocolException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class S3Uploader {

    private File file;
    private String s3_bucket, s3_key, s3_policy, s3_signature;

    private boolean interrupt;

    public S3Uploader(File file, CreateUpload.CreateUploadResponse createUploadResponse) {
        this(file, createUploadResponse.getS3_bucket(), createUploadResponse.getS3_key(),
                createUploadResponse.getS3_policy(), createUploadResponse.getS3_signature());
    }

    public S3Uploader(File file, String s3_bucket, String s3_key, String s3_policy, String s3_signature) {
        this.file = file;
        this.s3_bucket = s3_bucket;
        this.s3_key = s3_key;
        this.s3_policy = s3_policy;
        this.s3_signature = s3_signature;
    }

    public void cancelUpload() {
        interrupt = true;
    }

    /**
     * Executes a multipart form upload to the S3 Bucket as described in
     * <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/HTTPPOSTExamples.html">the AWS Documentation</a>.
     * @param s3UploadProgressListener An S3UploadProgressListener which is called whenever
     *                               bytes are written to the outgoing connection. May be null.
     * @throws IOException in case of a problem or the connection was aborted
     * @throws ClientProtocolException in case of an http protocol error
     */
    public void uploadFileToS3(S3UploadProgressListener s3UploadProgressListener)
            throws IOException, ClientProtocolException {
        //unfortunately, we can't use Unirest to execute the call, because there is no support
        //for Progress listeners (yet). See https://github.com/Mashape/unirest-java/issues/26

        int bufferSize = 1024;

        //opening a connection to the S3 Bucket
        HttpURLConnection urlConnection = (HttpURLConnection)new URL(s3_bucket).openConnection();
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setChunkedStreamingMode(bufferSize);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Cache-Control", "no-cache");
        String boundary = "*****";
        urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        //writing the request headers
        DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

        String newline = "\r\n";
        String twoHyphens = "--";
        dos.writeBytes(twoHyphens + boundary + newline);

        String attachmentName = "file";
        String attachmentFileName = "file";

        dos.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + newline);
        dos.writeBytes(newline);

        //sending the actual file
        byte[] buf = new byte[bufferSize];

        FileInputStream fis = new FileInputStream(file);
        long totalBytes = fis.getChannel().size();
        long writtenBytes = 0;

        int len;
        while((len = fis.read(buf)) != -1) {
            dos.write(buf);
            writtenBytes += len;

            s3UploadProgressListener.onProgressUpdated(new S3UpdateProgressEvent(writtenBytes, totalBytes, (float)((double)writtenBytes/totalBytes)));

            if(interrupt) {
                fis.close();
                dos.close();
                return;
            }
        }

        fis.close();

        //finish the call
        dos.writeBytes(newline);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + newline);

        dos.close();

        urlConnection.disconnect();
    }
}
