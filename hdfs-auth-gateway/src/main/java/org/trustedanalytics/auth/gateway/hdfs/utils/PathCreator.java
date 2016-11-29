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
package org.trustedanalytics.auth.gateway.hdfs.utils;

import org.apache.hadoop.fs.Path;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.Directory;

@Profile({Qualifiers.SIMPLE, Qualifiers.KERBEROS})
@Configuration
public class PathCreator {

  private static final String USER = "user";
  private static final String ORG_TEMPLATE = "\\{org\\}";
  private static final String USER_TEMPLATE = "\\{user\\}";

  public Path getOrgPath(Directory directory, String orgId) {
    return new Path(directory.getPath().toString().replaceAll(ORG_TEMPLATE, orgId));
  }

  public Path getOrgUserPath(Directory directory, String orgId, String userId) {
    return new Path(directory.getPath().toString().replaceAll(ORG_TEMPLATE, orgId).replaceAll(USER_TEMPLATE, userId));
  }

  public Path getUserHomePath(String user) {
    return createPath(USER, user);
  }

  private Path createPath(String... args) {
    return getPath(Path.SEPARATOR.concat(String.join(Path.SEPARATOR, args)));
  }

  private Path getPath(String relativePath) {
    return new Path(relativePath);
  }

}
