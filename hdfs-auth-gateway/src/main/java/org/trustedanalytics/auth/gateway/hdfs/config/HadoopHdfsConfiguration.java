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

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.utils.Qualifiers;

@Profile(Qualifiers.TEST_EXCLUDE)
@org.springframework.context.annotation.Configuration
public class HadoopHdfsConfiguration {

  @Autowired
  public ExternalConfiguration configuration;

  @Bean
  @Qualifier(Qualifiers.CONFIGURATION)
  public Configuration getHdfsConfiguration() throws IOException {
    Configuration config = new Configuration();
    String configurationPath = configuration.getConfigPath();
    config.addResource(new Path(configurationPath + "core-site.xml"));
    config.addResource(new Path(configurationPath + "hdfs-site.xml"));
    return config;
  }
}

