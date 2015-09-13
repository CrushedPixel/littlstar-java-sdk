package eu.crushedpixel.littlstar.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

public class Login {

    @Data
    @AllArgsConstructor
    public static class LoginSend implements ApiSend {
        /**
         * Username or E-Mail address
         */
        private String login;
        private String password;

        @Override
        public String getKey() {
            return "user";
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public class LoginResponse extends User {
        private String apikey;
        private String referral_code;
        private String email;
        private String gender;
        private int age;
        private String first_name;
        private String last_name;
        private String bio;
        private boolean featured;
        private boolean verified;
        private Date created_at;
        private Date updated_at;
        private String type;
        private String full_name;
        private int followers_count;
        private int following_count;
        private int videos_count;
        private int photos_count;
        private int channels_count;
    }

}
