package eu.crushedpixel.littlstar.api.data.upload;

import com.google.gson.annotations.SerializedName;

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
 * All Mime Types that are accepted for file uploads
 */
public enum MimeType {

    @SerializedName("video/mpeg")
    MPG,
    @SerializedName("video/mp4")
    MP4,
    @SerializedName("video/m4v")
    M4V,
    @SerializedName("video/avi")
    AVI,
    @SerializedName("video/ogg")
    OGG,
    @SerializedName("video/quicktime")
    MOV,
    @SerializedName("video/webm")
    WEBM,
    @SerializedName("video/flv")
    FLV,
    @SerializedName("image/jpeg")
    JPG,
    @SerializedName("image/png")
    PNG

}
