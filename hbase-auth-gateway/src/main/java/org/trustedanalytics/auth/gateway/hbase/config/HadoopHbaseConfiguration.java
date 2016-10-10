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
package org.trustedanalytics.auth.gateway.hbase.config;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hbase.utils.Qualifiers;

@org.springframework.context.annotation.Configuration
public class HadoopHbaseConfiguration {

  @Value("${hbase.configPath}")
  @NotNull
  private String configurationPath;

  @Bean
  @Profile(Qualifiers.TEST_EXCLUDE)
  @Qualifier(Qualifiers.CONFIGURATION)
  public Configuration getHbaseConfiguration() throws IOException {
    Configuration config = new Configuration();
    config.addResource(new Path(configurationPath + "core-site.xml"));
    config.addResource(new Path(configurationPath + "hdfs-site.xml"));
    config.addResource(new Path(configurationPath + "hbase-site.xml"));
    return config;
  }
}

