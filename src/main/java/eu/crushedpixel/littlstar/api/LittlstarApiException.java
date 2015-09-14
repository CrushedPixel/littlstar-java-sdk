package eu.crushedpixel.littlstar.api;

import eu.crushedpixel.littlstar.api.data.receive.Meta;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

@NoArgsConstructor
public class LittlstarApiException extends Exception {

    @Getter
    private Meta meta;

    public LittlstarApiException(Meta meta) {
        if(meta != null && meta.getCode() == 200) throw new IllegalArgumentException("Response code 200 is no error");
        this.meta = meta;
    }

    @Override
    public String getMessage() {
        if(meta == null) {
            return "Littlstar Api returned an invalid payload";
        }

        StringBuilder sb = new StringBuilder("Littlstar Api call returned response code ")
                .append(meta.getCode()).append("\n")
                .append("Errors: ");

        int i = 0;
        String[] errors = meta.getErrors().getErrorStrings();
        int size = errors.length;

        for(String error : errors) {
            sb.append(error);
            if(i < size-1) sb.append("\n");
            i++;
        }

        return sb.toString();
    }

}
