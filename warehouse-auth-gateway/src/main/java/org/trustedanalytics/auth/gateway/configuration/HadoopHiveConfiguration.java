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
package org.trustedanalytics.auth.gateway.configuration;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.KrbOrgWarehouseClient;
import org.trustedanalytics.auth.gateway.OrgWarehouseClient;
import org.trustedanalytics.auth.gateway.hive.HiveClientFactory;
import org.trustedanalytics.auth.gateway.impala.ImpalaClientFactory;
import org.trustedanalytics.auth.gateway.sentry.SentryClientFactory;
import org.trustedanalytics.auth.gateway.spi.Authorizable;
import org.trustedanalytics.auth.gateway.spi.OrgDecodingAuthorizable;
import org.trustedanalytics.auth.gateway.spi.OrgIdDecoder;
import org.trustedanalytics.auth.gateway.utils.Qualifiers;

@org.springframework.context.annotation.Configuration
public class HadoopHiveConfiguration {

  @Value("${hive.configPath}")
  @NotNull
  private String configurationPath;

  @Bean
  @Qualifier(Qualifiers.CONFIGURATION)
  public Configuration getHiveConfiguration() throws IOException {
    Configuration config = new Configuration();
    config.addResource(new Path(configurationPath + "core-site.xml"));
    config.addResource(new Path(configurationPath + "mapred-site.xml"));
    config.addResource(new Path(configurationPath + "hdfs-site.xml"));
    config.addResource(new Path(configurationPath + "yarn-site.xml"));
    return config;
  }

  @Profile(Qualifiers.SIMPLE)
  @Bean
  public Authorizable simpleWarehouseClient(HiveClientFactory hiveFactory, ImpalaClientFactory impalaFactory) {
    return new OrgDecodingAuthorizable(new OrgWarehouseClient(hiveFactory, impalaFactory), new OrgIdDecoder()::decode);
  }

  @Profile(Qualifiers.KERBEROS)
  @Bean
  public Authorizable krbWarehouseClient(SentryClientFactory sentryFactory, HiveClientFactory hiveFactory, ImpalaClientFactory impalaFactory) {
    return new OrgDecodingAuthorizable(new KrbOrgWarehouseClient(sentryFactory, hiveFactory, impalaFactory), new OrgIdDecoder()::decode);
  }
}
