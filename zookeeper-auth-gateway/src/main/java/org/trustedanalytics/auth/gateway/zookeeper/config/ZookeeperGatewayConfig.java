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

package org.trustedanalytics.auth.gateway.zookeeper.config;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.spi.Authorizable;
import org.trustedanalytics.auth.gateway.zookeeper.ZookeeperGateway;
import org.trustedanalytics.auth.gateway.zookeeper.client.KerberosfulZookeeperClient;
import org.trustedanalytics.auth.gateway.zookeeper.client.KerberoslessZookeeperClient;
import org.trustedanalytics.auth.gateway.zookeeper.utils.Qualifiers;

@Configuration
public class ZookeeperGatewayConfig {

  private static final String BASE_NODE = "/org";

  @Autowired
  private CuratorFramework curatorFramework;

  @Value("${zookeeper.user}")
  private String username;

  @Value("${zookeeper.brokerUser}")
  private String brokerUser;

  @Bean
  @Profile(Qualifiers.SIMPLE)
  public Authorizable createInSecureZookeeperGateway() throws IOException, LoginException {
    return new ZookeeperGateway(new KerberoslessZookeeperClient(curatorFramework, BASE_NODE), username, brokerUser);
  }

  @Bean
  @Profile(Qualifiers.KERBEROS)
  public Authorizable createSecureZookeeperGateway() throws IOException, LoginException {
    return new ZookeeperGateway(new KerberosfulZookeeperClient(curatorFramework, BASE_NODE), username, brokerUser);
  }
}
