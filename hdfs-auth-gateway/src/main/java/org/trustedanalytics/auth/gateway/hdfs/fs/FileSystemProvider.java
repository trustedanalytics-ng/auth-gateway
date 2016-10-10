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
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.google.common.base.Throwables;

public interface FileSystemProvider {

  FileSystem getFileSystem() throws IOException;

  default FileSystem getFileSystem(String user, Configuration configuration) throws IOException {
    try {
      return FileSystem.get(new URI(configuration.getRaw("fs.defaultFS")), configuration, user);
    } catch (InterruptedException | URISyntaxException | IOException e) {
      Throwables.propagateIfPossible(e, IOException.class);
      throw new IOException("Cannot create file system", e);
    }
  }

}
