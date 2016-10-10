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
package org.trustedanalytics.auth.gateway.engine;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.trustedanalytics.auth.gateway.cloud.uaa.AccessTokenResponse;
import org.trustedanalytics.auth.gateway.cloud.uaa.CachedUaaApiClient;
import org.trustedanalytics.auth.gateway.cloud.uaa.UaaApi;

@RunWith(Parameterized.class)
public class TokenCacheTest {

    private AccessTokenResponse expectedResponse;
    private UaaApi mockedUaaApi;

    @Before
    public void setUp() {
        expectedResponse = new AccessTokenResponse();
        expectedResponse.setAccessToken("bearer eyJhbGciOiJSUzI1NiJ9");
        mockedUaaApi = mock(UaaApi.class);
        when(mockedUaaApi.authenticate()).thenReturn(expectedResponse);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"access after given time", 0, 2 },
                {"access before given time", 10, 1 }
        });
    }

    @Parameterized.Parameter()
    public String description;

    @Parameterized.Parameter(1)
    public long duration;

    @Parameterized.Parameter(2)
    public int times;


    @Test
    public void testAccessTokenCache() {
        final UaaApi uaaApi = new CachedUaaApiClient(mockedUaaApi, duration, TimeUnit.MILLISECONDS);
        uaaApi.authenticate();
        AccessTokenResponse response = uaaApi.authenticate();
        verify(mockedUaaApi, times(times)).authenticate();
        assertEquals(expectedResponse, response);
    }
}
