/**
 * Copyright (c) 2016 Intel Corporation
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachedUaaApiClient implements UaaApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedUaaApiClient.class);
    private static final TimeUnit TIME_UNIT = SECONDS;
    private static final String CACHED_TOKEN = "CACHED_TOKEN";
    private static final long ACCESS_TIME = 60;
    private final LoadingCache<String, AccessTokenResponse> tokenCache;

    public CachedUaaApiClient(UaaApi uaaApi) {
        this(uaaApi, ACCESS_TIME, TIME_UNIT);
    }

    public CachedUaaApiClient(UaaApi uaaApi, long accessTime, TimeUnit timeUnit) {
        checkNotNull(uaaApi, "uaaApi");
        checkArgument(accessTime >= 0, "accessTime");
        checkNotNull(timeUnit, "timeUnit");

        this.tokenCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(accessTime, timeUnit)
                .build(new CacheLoader<String, AccessTokenResponse>() {
                    @Override
                    public AccessTokenResponse load(String key) throws Exception {
                        LOGGER.info("Refreshing token at: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                        return uaaApi.authenticate();
                    }
                });
    }

    @Override
    public AccessTokenResponse authenticate() {
        return tokenCache.getUnchecked(CACHED_TOKEN);
    }
}
