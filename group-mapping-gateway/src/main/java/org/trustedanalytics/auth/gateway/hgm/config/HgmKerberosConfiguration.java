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
package org.trustedanalytics.auth.gateway.hgm.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.trustedanalytics.auth.gateway.hgm.utils.Qualifiers;

@Configuration
@Profile(Qualifiers.KERBEROS)
public class HgmKerberosConfiguration {

  @Value("${group.mapping.kerberos.principal}")
  private String principal;

  @Value("${group.mapping.kerberos.keytabPath}")
  private String keytabPath;

  @Bean(name = "hgmRestTemplate")
  public RestTemplate getHgmKerberosRestClient() throws IOException {
    return new KerberosRestTemplate(keytabPath, principal);
  }
}
