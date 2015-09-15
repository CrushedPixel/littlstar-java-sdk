# Inofficial Littlstar Java SDK

---
## Introduction
This project is an inofficial Java Library to interact with the Littlstar API. So far, it only supports the API calls which are required to register and authenticate users, and all API calls required to upload a File to Littlstar.

For the official Littlstar API documentation, visit http://developer.littlstar.com/docs.

---
## Usage
To use the library, download the lastest binary release or compile it from source yourself using the provided Gradle Wrapper.

The Littlstar Java SDK is written in and it's official binary releases are compiled with **Java 6** to allow it to be used in projects that require an older Java SDK version.

---
## Dependencies
This project has the following dependencies:
- [Unirest 1.4.7](http://unirest.io/java.html) and its dependencies
- [Google Gson 2.2.4](https://github.com/google/gson)

To compile this project, you will also need [Project Lombok 1.16.6](http://projectlombok.org).

All of these dependencies are available via Maven, and are referenced by the project's `build.gradle` file.

---
## Code Examples
Every call to the Littlstar API can and should be made using an instance of `LittlstarApiClient`.

Example call to register a new user on Littlstar:

    LittlstarApiClient apiClient = new LittlstarApiClient();
    apiClient.register("USERNAME", "E-MAIL", "PASSWORD");

Every API call made with the `LittlstarApiClient` returns a `ResponseWrapper` object, containing the API's response. For more information about the returned objects, read the [official Littlstar API Documentation](http://developer.littlstar.com/docs).

If an API call fails due to connectivity problems, an `UnirestException` is being thrown.

If an API call fails because the API returns an error code or an invalid payload, a `LittlstarApiException` is being thrown, containing further information about the API's response for you to evaluate. You can prevent a `LittlstarApiClient` from validating the API's response by calling `apiClient.setThrowApiExceptions(false)`.
    
---
### Uploading files
#### Prerequisites
Access to the Littlstar upload endpoints must be requested by an application developer and approved by the Littlstar development team. Upon approval, an Application Token is generated for that application and can be revoked by Littlstar if abuse of their platform is detected, or if an application developer’s Token is in any way compromised. Please read the [Littlstar API Terms of Service](http://developer.littlstar.com/terms/) for more information.

To use a Littlstar Application Token to make file upload requests, you can either instantiate a `LittlstarApiClient` object using `new LittlstarApiClient("APPLICATION_TOKEN")` or set it using `apiClient.setApplicationToken("APPLICATION_TOKEN")`.

To be able to access private (user-specific) API endpoints (for example the upload endpoints), you need to authenticate the `LittlstarApiClient` first by logging in.

    LittlstarApiClient apiClient = new LittlstarApiClient();
    apiClient.login("USERNAME_OR_E-MAIL", "PASSWORD");

#### Creating a file upload
To start a file upload, you first need to send a request to the Littlstar API, to be assigned to a File Upload.

    ResponseWrapper<CreateUploadResponse> createUploadResponse = apiClient.createFileUpload(MimeType.MP4, "VIDEO TITLE");
    
The `ResponseWrapper` object returned by this method contains information about the newly created file upload which is required to execute the upload, for example the upload's ID.

#### Executing the upload
To actually execute the upload, the API Client needs to perform a file upload to Littlstar's Amazon S3 Bucket. The required connection information to access the S3 Bucket is contained in the `ResponseWrapper` returned by `createFileUpload()` as well. You can pass an `S3UploadProgressListener` to the call, whose `onProgressUpdated()` method is called whenever bytes are transferred to the S3 Service. If you intend to be able to cancel a running upload, you can pass an `S3UploadCancelHook` to the call.

    final S3UploadProgressListener uploadProgressListener = new S3UploadProgressListener() {
        @Override
        public void onProgressUpdated(S3UpdateProgressEvent updateProgressEvent) {
            System.out.println(updateProgressEvent.getProgressFloat());
        }
    };
    
    final S3UploadCancelHook uploadCancelHook = new S3UploadCancelHook();
    
    //asynchronously start the upload
    new Thread(new Runnable() {
        @Override
        public void run() {
             ResponseWrapper<UploadData> uploadFileResponse = apiClient.uploadFileToS3(file, createUploadResponse.getData(), uploadProgressListener, uploadCancelHook);
        }
    }).start();
            
    //after 10 seconds, cancel the upload (just an example)
    Thread.sleep(1000*10);
    uploadCancelHook.cancelS3Upload();

#### Updating a running file upload's data
While an upload is in progress, you can update the file's information (e.g. title, description and visibility) with incremental updates using the following call:

	//this only updates the file title and visibility, description and other fields are retained
    UpdateUploadData updateData = new UpdateUploadData("NEW_TITLE", null, null, false, null);
    apiClient.updateFileUpload(createUploadResponse.getData().getId(), updateData);

Please note that from the moment that the Littlstar API has been notified about the file upload being finished (which is automatically done by the `uploadFileToS3` call), updating the file information won't be possible anymore using this call.

#### Error handling
It is important that if a file upload to the S3 Bucket fails, and you do not intend to resume or restart it, you should inform the Littlstar API that the upload was canceled. If cancelling using an `S3UploadCancelHook`, this is done on your behalf automatically.

    try {
        ResponseWrapper<UploadData> uploadFileResponse = apiClient.uploadFileToS3(file, createUploadResponse.getData(), uploadProgressListener, uploadCancelHook);
    } catch(Exception e) {
        e.printStackTrace();
        
        //the file upload failed, so we tell the Littlstar API that the upload was canceled
        try {
            apiClient.cancelFileUpload(createUploadResponse.getData().getId());
        } catch(Exception e2) {
            //the upload couldn't be canceled, theres nothing more we can do
            e2.printStackTrace();
        }
    }

#### Working with a successful upload
Finally, all of the information about the uploaded file is returned in the `ResponseWrapper<UploadData>`, which the `uploadFileToS3()` call returns. To generate the URL for a file you uploaded, you can use the resource's slug:

    String resourceSlug = uploadFileResponse.getData().getResource().getSlug();
    String resourceURL = "http://littlstar.com/videos/" + resourceSlug;
    System.out.println(resourceURL);
    
Please keep in mind that depending on the file size, it may take some time for Littlstar to process the uploaded file, so the URL doesn't instantly become valid.

## License
This project is licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

## Donations
If you want to thank me for this project, feel free to donate any amount to my [PayPal Account](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=B8XRXQNP5QW7J).