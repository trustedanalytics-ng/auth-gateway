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
package org.trustedanalytics.auth.gateway.hdfs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclEntryType;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.auth.gateway.hdfs.config.ExternalConfiguration;
import org.trustedanalytics.auth.gateway.hdfs.fs.FileSystemProvider;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class HdfsClientTest {

  @Mock
  private FileSystemProvider fileSystemProvider;

  @Mock
  private ExternalConfiguration externalConfiguration;

  @Mock
  private FileSystem fileSystem;

  @InjectMocks
  public HdfsClient hdfsClient;

  private static final Path TEST_PATH = new Path("/org/test");

  private FsPermission userPermission;

  @Before
  public void init() throws IOException {
    userPermission = new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);
    when(fileSystemProvider.getFileSystem()).thenReturn(fileSystem);
  }

  @Test
  public void createDirectory_fileSystemMkdirsAndSetOwnerCalled_creationSuccess()
      throws IOException {
    when(fileSystem.exists(TEST_PATH)).thenReturn(false);

    hdfsClient.create(TEST_PATH, userPermission, "test_admin", "test");

    verify(fileSystem).exists(TEST_PATH);
    verify(fileSystem).mkdirs(TEST_PATH);
    verify(fileSystem).setPermission(TEST_PATH, userPermission);
    verify(fileSystem).setOwner(TEST_PATH, "test_admin", "test");
  }

  @Test()
  public void createDirectory_directoryAlreadyExists_updatePrivileges() throws IOException {
    when(fileSystem.exists(TEST_PATH)).thenReturn(true);

    FileStatus status = mock(FileStatus.class);
    FsPermission permission = mock(FsPermission.class);
    when(fileSystem.getFileStatus(TEST_PATH)).thenReturn(status);
    when(status.getPermission()).thenReturn(userPermission);
    when(status.getOwner()).thenReturn("test_admin");

    hdfsClient.create(TEST_PATH, userPermission, "test_admin", "test");
    verify(fileSystem, times(0)).mkdirs(TEST_PATH);
    verify(fileSystem).setPermission(TEST_PATH, userPermission);
    verify(fileSystem).setOwner(TEST_PATH, "test_admin", "test");
  }

  @Test
  public void deleteDirectory_fileSystemDeleteCalled_deletionSuccess() throws IOException {
    when(fileSystem.exists(TEST_PATH)).thenReturn(true);

    hdfsClient.remove(TEST_PATH);

    verify(fileSystem).exists(TEST_PATH);
    verify(fileSystem).delete(TEST_PATH, true);
  }

  @Test
  public void deleteDirectory_directoryNotExists_doNothing() throws IOException {
    when(fileSystem.exists(TEST_PATH)).thenReturn(false);

    hdfsClient.remove(TEST_PATH);
    verify(fileSystem, times(0)).mkdirs(TEST_PATH, userPermission);
    verify(fileSystem, times(0)).setOwner(TEST_PATH, "test_admin", "test");
  }

}
