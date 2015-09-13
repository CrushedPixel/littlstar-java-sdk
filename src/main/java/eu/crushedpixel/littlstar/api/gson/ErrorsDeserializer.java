package eu.crushedpixel.littlstar.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eu.crushedpixel.littlstar.api.data.receive.Errors;

import java.lang.reflect.Type;

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
 * A custom JsonDeserializer to handle the fact that the Littlstar API
 * may return both a String or a String Array as the "errors" value in a Meta object
 */
public class ErrorsDeserializer implements JsonDeserializer<Errors> {

    @Override
    public Errors deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonPrimitive()) return new Errors(new String[]{json.getAsString()});
        String[] errors = context.deserialize(json, String[].class);
        return new Errors(errors);
    }
}
