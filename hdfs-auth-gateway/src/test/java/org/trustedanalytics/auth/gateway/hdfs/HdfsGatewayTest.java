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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.trustedanalytics.auth.gateway.hdfs.config.DirectoryConfig;
import org.trustedanalytics.auth.gateway.hdfs.config.ExternalConfiguration;
import org.trustedanalytics.auth.gateway.hdfs.config.OrgConfig;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.OrgDirectory;
import org.trustedanalytics.auth.gateway.hdfs.fs.FileSystemProvider;
import org.trustedanalytics.auth.gateway.hdfs.kerberos.KerberosProperties;
import org.trustedanalytics.auth.gateway.hdfs.utils.PathCreator;
import org.trustedanalytics.auth.gateway.spi.AuthorizableGatewayException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HdfsClient.class)
public class HdfsGatewayTest {

  private static final String ORG = "test_org";

  private static final String USER = "test_user";

  private static final Path ORG_PATH = new Path("/org/test_org");

  private static final Path ORG_USERS_PATH = new Path("/org/test_org/user");

  private static final Path TMP_PATH = new Path("/org/test_org/tmp");

  private static final Path USER_PATH = new Path("/org/test_org/user/test_user");

  private static final Path USER_HOME_PATH = new Path("/user/test_user");

  private static final FsPermission userPermission =
      new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);

  private static final List<AclEntry> defAcl =
      Arrays.asList(AclEntry.parseAclEntry("user:broker:--x", true));

  @Mock
  private FileSystem fileSystem;

  @Mock
  private FileStatus status;

  @Mock
  private FileSystemProvider fileSystemProvider;

  @Mock
  private HdfsClient hdfsClient;

  @Mock
  private PathCreator pathCreator;

  @Mock
  private KerberosProperties krbProperties;

  @Mock
  private ExternalConfiguration config;

  @Mock
  private DirectoryConfig directoryConfig;

  @InjectMocks
  private HdfsGateway hdfsGateway;

  @Before
  public void init() throws IOException {
    when(fileSystemProvider.getFileSystem()).thenReturn(fileSystem);
    PowerMockito.spy(HdfsClient.class);
    PowerMockito.when(HdfsClient.getInstance(fileSystem)).thenReturn(hdfsClient);

    when(fileSystem.getFileStatus(any())).thenReturn(status);
    when(status.getPermission())
        .thenReturn(new FsPermission(FsAction.NONE, FsAction.NONE, FsAction.NONE));

    List<OrgDirectory> others = Arrays.asList(
        new OrgDirectory(TMP_PATH, userPermission, defAcl),
        new OrgDirectory(ORG_USERS_PATH, userPermission, defAcl));
    OrgDirectory root =
        new OrgDirectory(new Path("/org/{org}"), userPermission, Collections.emptyList());
    OrgDirectory template = new OrgDirectory(new Path("/org/{org}/user/{user}"), userPermission,
        Collections.emptyList());

    when(directoryConfig.getOrg()).thenReturn(new OrgConfig(root, template, others));
    when(pathCreator.getOrgPath(root, "test_org")).thenReturn(ORG_PATH);
    when(pathCreator.getOrgUserPath(template, "test_org", "test_user")).thenReturn(USER_PATH);
    when(pathCreator.getUserHomePath("test_user")).thenReturn(USER_HOME_PATH);
  }

  @Test
  public void addOrganization_createDirectoryCalled_creationSuccess()
      throws AuthorizableGatewayException, IOException {

    hdfsGateway.addOrganization(ORG);
    verify(hdfsClient).create(ORG_PATH, userPermission, "test_org_admin", "test_org");
    verify(hdfsClient).modifyAcl(ORG_PATH, Collections.emptyList());

    verify(hdfsClient).create(ORG_USERS_PATH, userPermission, "test_org_admin", "test_org");
    verify(hdfsClient).modifyAcl(ORG_USERS_PATH, defAcl);
    verify(hdfsClient).modifyPermissionsRecursively(ORG_USERS_PATH, "test_org", userPermission);
    verify(hdfsClient).modifyAclRecursively(ORG_USERS_PATH, defAcl);

    verify(hdfsClient).create(TMP_PATH, userPermission, "test_org_admin", "test_org");
    verify(hdfsClient).modifyAcl(TMP_PATH, defAcl);
    verify(hdfsClient).modifyPermissionsRecursively(TMP_PATH, "test_org", userPermission);
    verify(hdfsClient).modifyAclRecursively(TMP_PATH, defAcl);
  }

  @Test(expected = AuthorizableGatewayException.class)
  public void addOrganization_hdfsClientThrowIOException_throwAuthorizableGatewayException()
      throws AuthorizableGatewayException, IOException {
    doThrow(new IOException()).when(hdfsClient).create(ORG_PATH, userPermission, "test_org_admin",
        "test_org");
    hdfsGateway.addOrganization(ORG);
  }

  @Test
  public void removeOrganization_deleteDirectoryCalled_deleteDirectoryMethodCalled()
      throws AuthorizableGatewayException, IOException {
    hdfsGateway.removeOrganization(ORG);
    verify(hdfsClient).remove(ORG_PATH);
  }

  @Test(expected = AuthorizableGatewayException.class)
  public void removeOrganization_hdfsClientThrowIOException_throwAuthorizableGatewayException()
      throws AuthorizableGatewayException, IOException {
    doThrow(new IOException()).when(hdfsClient).remove(ORG_PATH);
    hdfsGateway.removeOrganization(ORG);
  }

  @Test
  public void addUserToOrg_createDirectoryCalled_creationSuccess()
      throws AuthorizableGatewayException, IOException {
    hdfsGateway.addUserToOrg(USER, ORG);
    verify(hdfsClient).create(USER_PATH, userPermission, "test_user", "test_org");
    verify(hdfsClient).create(USER_HOME_PATH, userPermission, "test_user", "test_org");
  }

  @Test(expected = AuthorizableGatewayException.class)
  public void addUserToOrg_hdfsClientThrowIOException_throwAuthorizableGatewayException()
      throws AuthorizableGatewayException, IOException {
    doThrow(new IOException()).when(hdfsClient).create(USER_PATH, userPermission, "test_user", "test_org");
    hdfsGateway.addUserToOrg(USER, ORG);
  }

  @Test
  public void removeUserFromOrg_deleteDirectoryCalled_deleteDirectoryMethodCalled()
      throws AuthorizableGatewayException, IOException {
    hdfsGateway.removeUserFromOrg(USER, ORG);
    verify(hdfsClient).remove(USER_PATH);
  }

  @Test(expected = AuthorizableGatewayException.class)
  public void removeUserFromOrg_hdfsClientThrowIOException_throwAuthorizableGatewayException()
      throws AuthorizableGatewayException, IOException {
    doThrow(new IOException()).when(hdfsClient).remove(USER_PATH);
    hdfsGateway.removeUserFromOrg(USER, ORG);
  }
}
