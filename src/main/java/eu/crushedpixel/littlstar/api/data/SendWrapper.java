package eu.crushedpixel.littlstar.api.data;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

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
 * A wrapper class to create JSON Payloads to be sent to the Littlstar API
 */
@Data
@RequiredArgsConstructor
public class SendWrapper<T extends ApiSend> {

    private final T wrapped;

    private transient FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
        @Override
        public String translateName(Field f) {
            //if the wrapped object is about to be serialized,
            //use its getKey() method to define the JSON key's name
            try {
                if(f.getType().equals(ApiSend.class) && f.get(SendWrapper.this).equals(wrapped)) return wrapped.getKey();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return f.getName();
        }
    };

    private transient Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(fieldNamingStrategy)
            .create();

    public String toJson() {
        if(wrapped.getKey() == null) return null;
        return gson.toJson(this);
    }

}
