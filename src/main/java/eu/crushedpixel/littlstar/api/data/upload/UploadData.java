package eu.crushedpixel.littlstar.api.data.upload;

import eu.crushedpixel.littlstar.api.data.resource.FileResource;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

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
public class UploadData {

    private int id;
    private UUID uuid;
    private String status;
    private int progress;
    private MimeType mime_type;
    private Date created_at;
    private Date updated_at;
    private Date started_at;
    private Date completed_at;
    private FileResource resource;

}
