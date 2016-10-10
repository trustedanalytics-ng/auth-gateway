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
package org.trustedanalytics.auth.gateway.hdfs.utils;

public final class Qualifiers {
  public static final String SIMPLE = "hdfs-auth-gateway";
  public static final String KERBEROS = "krb-hdfs-auth-gateway";
  public static final String CONFIGURATION = "hdfs-configuration";
  public static final String TEST_EXCLUDE = "!test";
  public static final String TEST = "test";

  private Qualifiers() {
  }
}
