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
package org.trustedanalytics.auth.gateway.cloud;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.trustedanalytics.auth.gateway.cloud.api.OrgApiResponse;
import org.trustedanalytics.auth.gateway.cloud.api.UserApiResponse;

import java.util.List;

@Headers("Content-Type: application/json")
public interface CloudApi {
  @RequestLine("GET /rest/orgs")
  List<OrgApiResponse> getOrganizations();

  @RequestLine("GET /rest/orgs/{org}/users")
  List<UserApiResponse> getOrganizationUsers(@Param("org") String id);
}
