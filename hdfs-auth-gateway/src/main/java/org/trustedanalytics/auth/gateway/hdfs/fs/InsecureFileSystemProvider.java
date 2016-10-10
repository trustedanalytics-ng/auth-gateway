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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.config.ExternalConfiguration;
import org.trustedanalytics.auth.gateway.hdfs.utils.Qualifiers;

@Profile(Qualifiers.SIMPLE)
@org.springframework.context.annotation.Configuration
public class InsecureFileSystemProvider implements FileSystemProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecureFileSystemProvider.class);

  @Autowired
  private ExternalConfiguration externalConfiguration;

  @Autowired
  @Qualifier(Qualifiers.CONFIGURATION)
  private Configuration configuration;

  @Override
  public FileSystem getFileSystem() throws IOException {
    LOGGER.info(String.format("Get fileSystem as : %s", externalConfiguration.getSuperUser()));
    return this.getFileSystem(externalConfiguration.getSuperUser(), configuration);
  }
}
