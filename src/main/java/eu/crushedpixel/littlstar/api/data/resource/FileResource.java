package eu.crushedpixel.littlstar.api.data.resource;

import eu.crushedpixel.littlstar.api.data.User;
import lombok.AllArgsConstructor;
import lombok.Data;

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

@Data
@AllArgsConstructor
public class FileResource {
    
    private int id;
    private ResourceType type;
    private String slug;
    private String title;
    private String description;
    private ResourceVisibility visibility;
    private boolean sponsored;
    private boolean featured;
    private boolean vr_optimized;
    private int duration;
    private int views;
    private boolean download;
    private int stars;
    private boolean stared;
    private Date created_at;
    private Date updated_at;
    private String[] hashtag_list;
    private ResourceVersions versions;
    private ResourcePosters posters;
    private ResourceBanners banners;
    private User user;

}
