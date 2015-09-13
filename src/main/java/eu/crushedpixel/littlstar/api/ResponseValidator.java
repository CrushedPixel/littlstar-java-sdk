package eu.crushedpixel.littlstar.api;

import eu.crushedpixel.littlstar.api.data.receive.ResponseWrapper;

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

public class ResponseValidator {

    public boolean validateResponse(ResponseWrapper responseWrapper, boolean throwException) throws LittlstarApiException {
        if(responseWrapper == null || responseWrapper.getMeta() == null) {
            if(throwException) throw new LittlstarApiException();
            return false;
        }
        if(responseWrapper.getMeta().getCode() != 200) {
            if(throwException) throw new LittlstarApiException(responseWrapper.getMeta());
            return false;
        }

        return true;
    }

}
