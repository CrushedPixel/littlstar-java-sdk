package eu.crushedpixel.littlstar.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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

/**
 * The Littlstar API returns dates as Strings formatted by Ruby.<br>
 * To deserialize it using a Pattern, the following Pattern is required: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"<br>
 * However, the 'X' Pattern character has only been added in Java 7, which is why we need a custom JsonDeserializer
 * (because for some reason the GsonBuilder doesn't accept SimpleDateFormats in its setDateFormat() method)
 */
public class RubyDateDeserializer implements JsonDeserializer<Date> {

    private static final SimpleDateFormat RUBY_DATE_FORMAT =

            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ") {

                @Override
                public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
                    StringBuffer defaultFormat = super.format(date, toAppendTo, pos);
                    return defaultFormat.insert(defaultFormat.length() - 2, ":");
                }

                @Override
                public Date parse(String text, ParsePosition pos) {
                    if(text.length() > 3) {
                        text = text.substring(0, text.length() - 3) + text.substring(text.length() - 2);
                    }
                    return super.parse(text, pos);
                }
            };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return RUBY_DATE_FORMAT.parse(json.getAsString());
        } catch(ParseException e) {
            throw new JsonParseException(e);
        }
    }
}
