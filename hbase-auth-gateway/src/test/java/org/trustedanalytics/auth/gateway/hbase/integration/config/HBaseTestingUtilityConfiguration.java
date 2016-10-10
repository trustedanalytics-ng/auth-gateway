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

package org.trustedanalytics.auth.gateway.hbase.integration.config;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.trustedanalytics.auth.gateway.hbase.utils.Qualifiers;

@ActiveProfiles(Qualifiers.TEST)
@Configuration
public class HBaseTestingUtilityConfiguration {

  @Autowired
  @Qualifier(Qualifiers.SIMPLE)
  private org.apache.hadoop.conf.Configuration configuration;

  @Bean(destroyMethod = "shutdownMiniCluster")
  public HBaseTestingUtility getHBaseTestingUtility() throws Exception {
    HBaseTestingUtility utility = new HBaseTestingUtility(configuration);

    utility.startMiniCluster();
    return utility;
  }
}
