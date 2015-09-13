package eu.crushedpixel.littlstar.api.gson;

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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.crushedpixel.littlstar.api.LittlstarApiClient;
import eu.crushedpixel.littlstar.api.data.receive.Meta;
import eu.crushedpixel.littlstar.api.data.receive.Pagination;
import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;

import java.io.IOException;

/**
 * A custom TypeAdapter to properly deserialize the JSON
 * the Littlstar API returns into ResponseWrapper objects<br>
 *
 * Because of type erasure, GSON would return a ResponseWrapper object
 * containing a {@link com.google.gson.internal.LinkedTreeMap} instead of the desired wrapped object.
 */
public class ResponseWrapperTypeAdapter<T> extends TypeAdapter<ResponseWrapper<T>> {

    private Class<T> wrappedClass;

    public ResponseWrapperTypeAdapter(Class<T> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }

    @Override
    public ResponseWrapper<T> read(JsonReader in) throws IOException {
        Meta meta = null;
        Pagination pagination = null;
        T data = null;

        in.beginObject();

        while(in.hasNext()) { //iterate over all array entries
            String key = in.nextName();

            if("meta".equals(key)) {
                meta = LittlstarApiClient.GSON.fromJson(in, Meta.class);

            } else if("pagination".equals(key)) {
                pagination = LittlstarApiClient.GSON.fromJson(in, Pagination.class);

            } else if("data".equals(key)) {
                data = LittlstarApiClient.GSON.fromJson(in, wrappedClass);
            }
        }

        in.endObject();

        return new ResponseWrapper<T>(meta, pagination, data);
    }

    @Override
    public void write(JsonWriter out, ResponseWrapper<T> value) throws IOException {
        //we don't need to write ResponseWrapper objects
    }

}
