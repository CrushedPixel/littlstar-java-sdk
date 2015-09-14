package eu.crushedpixel.littlstar.api.upload.progress;

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

public interface UploadProgressListener {

    /**
     * This method is called by an {@link org.apache.http.HttpRequest}
     * using a {@link eu.crushedpixel.littlstar.api.upload.CountingFileBody} element
     * @param progressUpdateEvent The ProgressUpdateEvent, containing information about the upload's progress
     */
    void onProgressUpdated(ProgressUpdateEvent progressUpdateEvent);

}
