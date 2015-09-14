package eu.crushedpixel.littlstar.api.upload;

import eu.crushedpixel.littlstar.api.upload.progress.ProgressUpdateEvent;
import eu.crushedpixel.littlstar.api.upload.progress.UploadProgressListener;
import lombok.Getter;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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

public class CountingFileBody extends FileBody {

    @Getter
    private long fileSize;

    @Getter
    private UploadProgressListener uploadProgressListener;

    public CountingFileBody(File file, UploadProgressListener uploadProgressListener) {
        super(file);
        this.fileSize = file.length();
        this.uploadProgressListener = uploadProgressListener;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        CountingOutputStream countingOutputStream = new CountingOutputStream(out) {
            @Override
            protected void beforeWrite(int n) {
                super.beforeWrite(n);
                if (uploadProgressListener != null && n != 0)
                    uploadProgressListener.onProgressUpdated(new ProgressUpdateEvent(getByteCount(), fileSize));
            }
        };
        super.writeTo(countingOutputStream);
    }

}