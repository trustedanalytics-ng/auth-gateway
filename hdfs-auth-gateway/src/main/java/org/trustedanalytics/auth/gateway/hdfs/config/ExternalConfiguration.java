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
package org.trustedanalytics.auth.gateway.hdfs.config;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.utils.Qualifiers;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class ExternalConfiguration {

  @Value("${hdfs.keytabPath}")
  @Getter @Setter
  private String keytabPath;

  @Value("${hdfs.superUser}")
  @NotNull
  @Getter @Setter
  private String superUser;

  @Value("${hdfs.cfUser}")
  @NotNull
  @Getter @Setter
  private String cfUser;

  @Value("${hdfs.hiveUser}")
  @NotNull
  @Getter @Setter
  private String hiveUser;

  @Value("${hdfs.vcapUser}")
  @NotNull
  @Getter @Setter
  private String vcapUser;

  @Value("${hdfs.arcadiaUser}")
  @NotNull
  @Getter @Setter
  private String arcadiaUser;

  @Value("${hdfs.configPath}")
  @NotNull
  @Getter @Setter
  private String configPath;
}
