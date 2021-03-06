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

import org.trustedanalytics.auth.gateway.hdfs.config.dir.OrgDirectory;

import lombok.Getter;
import lombok.Setter;

public final class OrgConfig {

  @Getter
  private List<OrgDirectory> dirs = new ArrayList<>();
  @Getter @Setter
  private OrgDirectory root;
  @Getter @Setter
  private OrgDirectory user;

  public OrgConfig() {

  }

  public OrgConfig(OrgDirectory root, OrgDirectory user, List<OrgDirectory> dirs) {
    this.dirs = dirs;
    this.root = root;
    this.user = user;
  }
}
