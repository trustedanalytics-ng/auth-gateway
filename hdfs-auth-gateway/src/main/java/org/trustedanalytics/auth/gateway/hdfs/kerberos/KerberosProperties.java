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
package org.trustedanalytics.auth.gateway.hdfs.kerberos;

import lombok.Data;

@Data
public class KerberosProperties {

  private final String kdc;
  private final String realm;
  private final String technicalPrincipal;
  private final String keytabPrincipal;
  private final String keytabPath;

  public KerberosProperties(String kdc, String realm, String technicalUser, String keytabPrincipal,
      String keytabPath) {
    this.realm = realm;
    this.kdc = kdc;
    this.technicalPrincipal = technicalUser;
    this.keytabPrincipal = keytabPrincipal;
    this.keytabPath = keytabPath;
  }

}
