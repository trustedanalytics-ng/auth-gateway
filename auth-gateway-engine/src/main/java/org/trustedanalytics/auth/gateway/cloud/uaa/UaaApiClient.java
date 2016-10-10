/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.trustedanalytics.auth.gateway.cloud.uaa;

import static com.google.common.base.Preconditions.checkNotNull;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

public class UaaApiClient implements UaaApi {

    private final UaaApi uaaApi;

    public UaaApiClient(String clientId, String clientPassword, String uaaApi) {
        checkNotNull(clientId, "clientId is required");
        checkNotNull(clientPassword, "clientPassword is required");
        checkNotNull(uaaApi, "uaaApi url is required");

        this.uaaApi = Feign.builder().encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(UaaApi.class))
                .requestInterceptor(new BasicAuthRequestInterceptor(clientId, clientPassword))
                .logLevel(feign.Logger.Level.BASIC)
                .target(UaaApi.class, uaaApi);
    }

    @Override
    public AccessTokenResponse authenticate() {
        return uaaApi.authenticate();
    }
}
