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
package org.trustedanalytics.auth.gateway.hgm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.trustedanalytics.auth.gateway.hgm.HgmGateway;
import org.trustedanalytics.auth.gateway.hgm.utils.Qualifiers;
import org.trustedanalytics.auth.gateway.spi.Authorizable;
import org.trustedanalytics.auth.gateway.spi.CompositeAuthorizable;
import org.trustedanalytics.auth.gateway.spi.OrgDecodingAuthorizable;
import org.trustedanalytics.auth.gateway.spi.OrgIdDecoder;

@Configuration
@Profile({Qualifiers.SIMPLE, Qualifiers.KERBEROS})
public class HgmConfig {

    @Value("${group.mapping.url}")
    private String groupMappingServiceUrl;

    @Value("${group.mapping.supergroup}")
    private String supergroupName;

    @Bean
    public Authorizable hgmGateway(RestTemplate hgmRestTemplate) {
        Authorizable uuidGateway = new HgmGateway(groupMappingServiceUrl, supergroupName, hgmRestTemplate);
        Authorizable idGateway = new OrgDecodingAuthorizable(new HgmGateway(groupMappingServiceUrl, supergroupName, hgmRestTemplate), new OrgIdDecoder()::decode);
        return new CompositeAuthorizable(uuidGateway, idGateway);
    }
}
