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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.DefaultDirectory;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.Directory;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.OrgDirectory;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix="hdfs.structure")
public class DirectoryConfig {

  @Getter
  private final List<DefaultDirectory> base = new ArrayList<>();

  @NestedConfigurationProperty
  @Getter @Setter
  private OrgConfig org;

}
