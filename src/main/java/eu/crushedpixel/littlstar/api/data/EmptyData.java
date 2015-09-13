package eu.crushedpixel.littlstar.api.data;

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
 * An empty object that can be used as a placeholder for certain HTTP Requests (e.g. <b>DELETE</b>),
 * or when the Littlstar API returns an empty JSON Array as "data" value
 */
public class EmptyData implements ApiSend {

    @Override
    public String getKey() {
        return null;
    }

}
