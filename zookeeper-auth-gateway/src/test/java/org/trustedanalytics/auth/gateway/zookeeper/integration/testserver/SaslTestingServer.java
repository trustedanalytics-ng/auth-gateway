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

package org.trustedanalytics.auth.gateway.zookeeper.integration.testserver;

import java.io.IOException;

import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingZooKeeperServer;

public class SaslTestingServer {

  private final TestingZooKeeperServer testingZooKeeperServer;
  private final String connectionString;

  public SaslTestingServer() throws Exception {
    InstanceSpec instanceSpec = InstanceSpec.newInstanceSpec();
    testingZooKeeperServer =
        new TestingZooKeeperServer(new TestZkQuorumConfigBuilder(instanceSpec));
    testingZooKeeperServer.start();
    connectionString = instanceSpec.getConnectString();
  }

  public String getConnectionString() {
    return connectionString;
  }

  public void stop() throws IOException {
    testingZooKeeperServer.stop();
  }
}
