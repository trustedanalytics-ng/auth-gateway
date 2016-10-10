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
import java.net.URISyntaxException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.SystemEnvironment;
import org.trustedanalytics.auth.gateway.hbase.kerberos.KerberosHbaseProperties;
import org.trustedanalytics.auth.gateway.hbase.utils.Qualifiers;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManager;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManagerFactory;
import sun.security.krb5.KrbException;

@Profile(Qualifiers.TEST_EXCLUDE)
@org.springframework.context.annotation.Configuration
public class HbaseConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(HbaseConfiguration.class);

  @Autowired
  private KerberosHbaseProperties kerberosHbaseProperties;

  @Autowired
  @Qualifier(Qualifiers.CONFIGURATION)
  private Configuration hbaseConfiguration;

  @Profile(Qualifiers.SIMPLE)
  @Bean(destroyMethod = "close")
  public Connection getInsecureHbaseConnection()
      throws InterruptedException, URISyntaxException, LoginException, IOException {
    LOGGER.info("Creating hbase client without kerberos support");
    return getInsecuredHBaseClient(hbaseConfiguration);
  }

  @Profile(Qualifiers.KERBEROS)
  @Bean(destroyMethod = "close")
  public Connection getSecureHBaseConnection()
      throws InterruptedException, URISyntaxException, LoginException, IOException, KrbException {
    LOGGER.info("Creating hbase client with kerberos support");
    return getSecuredHBaseClient(hbaseConfiguration);
  }

  private Connection getInsecuredHBaseClient(Configuration hbaseConf)
      throws InterruptedException, URISyntaxException, LoginException, IOException {
    SystemEnvironment systemEnvironment = new SystemEnvironment();
    Configuration conf = HBaseConfiguration.create(hbaseConf);
    User user = UserProvider.instantiate(hbaseConf).create(UserGroupInformation
        .createRemoteUser(systemEnvironment.getVariable(SystemEnvironment.KRB_USER)));
    return ConnectionFactory.createConnection(conf, user);
  }

  private Connection getSecuredHBaseClient(Configuration hbaseConf)
      throws InterruptedException, URISyntaxException, LoginException, IOException, KrbException {
    LOGGER.info("Trying kerberos authentication");
    KrbLoginManager loginManager = KrbLoginManagerFactory.getInstance().getKrbLoginManagerInstance(
        kerberosHbaseProperties.getKdc(), kerberosHbaseProperties.getRealm());

    SystemEnvironment systemEnvironment = new SystemEnvironment();
    Subject subject =
        loginManager.loginWithKeyTab(systemEnvironment.getVariable(SystemEnvironment.KRB_USER),
            systemEnvironment.getVariable(SystemEnvironment.KRB_KEYTAB));
    loginManager.loginInHadoop(subject, hbaseConf);
    Configuration conf = HBaseConfiguration.create(hbaseConf);
    User user =
        UserProvider.instantiate(conf).create(UserGroupInformation.getUGIFromSubject(subject));
    return ConnectionFactory.createConnection(conf, user);
  }
}
