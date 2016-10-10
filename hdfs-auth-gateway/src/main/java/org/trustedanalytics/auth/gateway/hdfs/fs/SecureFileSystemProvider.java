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
package org.trustedanalytics.auth.gateway.hdfs.fs;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.kerberos.KerberosProperties;
import org.trustedanalytics.auth.gateway.hdfs.utils.Qualifiers;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManager;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManagerFactory;

import com.google.common.base.Throwables;

import sun.security.krb5.KrbException;

@Profile(Qualifiers.KERBEROS)
@org.springframework.context.annotation.Configuration
public class SecureFileSystemProvider implements FileSystemProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecureFileSystemProvider.class);

  @Autowired
  @Qualifier(Qualifiers.CONFIGURATION)
  private Configuration configuration;

  @Autowired
  private KerberosProperties krbProperties;

  @Override
  public FileSystem getFileSystem() throws IOException {
    try {
      logInKerberos();
    } catch (KrbException | LoginException | IOException e) {
      LOGGER.error("Authorization to kerberos failed", e);
      Throwables.propagateIfPossible(e, IOException.class);
      throw new IOException("Authorization to kerberos failed", e);
    }
    return this.getFileSystem(krbProperties.getKeytabPrincipal(), configuration);
  }

  private void logInKerberos() throws LoginException, KrbException, IOException {
    LOGGER.info(String.format("Trying authorization with kerberos as: %s using keytab",
        krbProperties.getKeytabPrincipal()));

    KrbLoginManager loginManager = KrbLoginManagerFactory.getInstance()
        .getKrbLoginManagerInstance(krbProperties.getKdc(), krbProperties.getRealm());

    loginManager.loginInHadoop(loginManager.loginWithKeyTab(krbProperties.getKeytabPrincipal(),
        krbProperties.getKeytabPath()), configuration);
  }

}
