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
package org.trustedanalytics.auth.gateway.engine.integration.tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.cloud.Cloud;
import org.trustedanalytics.auth.gateway.cloud.CloudApi;
import org.trustedanalytics.auth.gateway.cloud.api.OrgApiResponse;
import org.trustedanalytics.auth.gateway.cloud.api.UserApiResponse;

@Profile("test")
public class CloudConfig {

  private CloudApi cloudApi;

  private List<OrgApiResponse> createOrgResource(String... orgs) {
    return Arrays.asList(orgs).stream().map(org -> new OrgApiResponse(org, org))
        .collect(Collectors.toList());
  }

  private List<UserApiResponse> createUserResource(String... users) {
    return Arrays.asList(users).stream().map(user -> new UserApiResponse(user, user, "true"))
        .collect(Collectors.toList());
  }

  @Bean
  public Cloud getCloud() {
    cloudApi = mock(CloudApi.class);

    when(cloudApi.getOrganizations()).thenReturn(
        createOrgResource(AuthGatewayControllerTest.ORG_ID, AuthGatewayControllerTest.ORG1_ID));
    when(cloudApi.getOrganizationUsers(AuthGatewayControllerTest.ORG_ID)).thenReturn(
        createUserResource(AuthGatewayControllerTest.USER_ID, AuthGatewayControllerTest.USER1_ID));
    when(cloudApi.getOrganizationUsers(AuthGatewayControllerTest.ORG1_ID)).thenReturn(
        createUserResource(AuthGatewayControllerTest.USER_ID, AuthGatewayControllerTest.USER1_ID));

    return new Cloud(cloudApi);
  }

}
