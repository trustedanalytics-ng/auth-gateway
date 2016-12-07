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

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.auth.gateway.hdfs.config.DirectoryConfig;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.DefaultDirectory;
import org.trustedanalytics.auth.gateway.hdfs.config.dir.Directory;
import org.trustedanalytics.auth.gateway.hdfs.fs.FileSystemProvider;
import org.trustedanalytics.auth.gateway.hdfs.utils.PathCreator;
import org.trustedanalytics.auth.gateway.hdfs.utils.Qualifiers;
import org.trustedanalytics.auth.gateway.spi.Authorizable;
import org.trustedanalytics.auth.gateway.spi.AuthorizableGatewayException;

@Profile({Qualifiers.SIMPLE, Qualifiers.KERBEROS})
@Configuration
public class HdfsGateway implements Authorizable {

  private static final String NAME = "hdfs";

  private static final String ADMIN_POSTFIX = "_admin";

  @Autowired
  private PathCreator paths;

  @Autowired
  private DirectoryConfig dirConfig;

  @Autowired
  private FileSystemProvider fileSystemProvider;

  @Override
  public void addOrganization(String orgId) throws AuthorizableGatewayException {
    try (final FileSystem fileSystem = fileSystemProvider.getFileSystem()) {
      final HdfsClient fsClient = HdfsClient.getInstance(fileSystem);

      String orgUser = orgId.concat(ADMIN_POSTFIX);
      String orgGroup = orgId;
      Path orgPath = paths.getOrgPath(dirConfig.getOrg().getRoot(), orgId);
      fsClient.create(orgPath, dirConfig.getOrg().getRoot().getPermissions(), orgUser, orgGroup);
      fsClient.modifyAcl(orgPath, dirConfig.getOrg().getRoot().getAcl());
      fsClient.setWorkingDir(orgPath);

      for (Directory dir : dirConfig.getOrg().getDirs()) {
        fsClient.create(dir.getPath(), dir.getPermissions(), orgUser, orgGroup);
        fsClient.modifyAcl(dir.getPath(), dir.getAcl());
        fsClient.modifyPermissionsRecursively(dir.getPath(), orgGroup, dir.getPermissions());
        fsClient.modifyAclRecursively(dir.getPath(), dir.getAcl());
      }
    } catch (IOException e) {
      throw new AuthorizableGatewayException(String.format("Can't add organization: %s", orgId), e);
    }
  }

  @Override
  public void removeOrganization(String orgId) throws AuthorizableGatewayException {
    try (final FileSystem fileSystem = fileSystemProvider.getFileSystem()) {
      final HdfsClient fsClient = HdfsClient.getInstance(fileSystem);
      fsClient.remove(paths.getOrgPath(dirConfig.getOrg().getRoot(), orgId));
    } catch (IOException e) {
      throw new AuthorizableGatewayException(String.format("Can't remove organization: %s", orgId),
          e);
    }
  }

  @Override
  public void addUserToOrg(String userId, String orgId) throws AuthorizableGatewayException {
    try (final FileSystem fileSystem = fileSystemProvider.getFileSystem()) {
      final HdfsClient fsClient = HdfsClient.getInstance(fileSystem);
      Path orgUserPath = paths.getOrgUserPath(dirConfig.getOrg().getUser(), orgId, userId);
      fsClient.create(orgUserPath, dirConfig.getOrg().getUser().getPermissions(), userId, orgId);
      fsClient.modifyAcl(orgUserPath, dirConfig.getOrg().getUser().getAcl());

      fsClient.create(paths.getUserHomePath(userId),
          new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE), userId, orgId);
    } catch (IOException e) {
      throw new AuthorizableGatewayException(String.format("Can't add user: %s", userId), e);
    }
  }

  @Override
  public void removeUserFromOrg(String userId, String orgId) throws AuthorizableGatewayException {
    try (final FileSystem fileSystem = fileSystemProvider.getFileSystem()) {
      final HdfsClient fsClient = HdfsClient.getInstance(fileSystem);
      Path orgUserPath = paths.getOrgUserPath(dirConfig.getOrg().getUser(), orgId, userId);
      fsClient.remove(orgUserPath);
    } catch (IOException e) {
      throw new AuthorizableGatewayException(
          String.format("Can't remove user: %s from org: %s", userId, orgId), e);
    }
  }

  @Override
  public void synchronize() throws AuthorizableGatewayException {
    try (final FileSystem fileSystem = fileSystemProvider.getFileSystem()) {
      final HdfsClient fsClient = HdfsClient.getInstance(fileSystem);
      for (DefaultDirectory dir : dirConfig.getBase()) {
        fsClient.create(dir.getPath(), dir.getPermissions(), dir.getUser(), dir.getGroup());
        fsClient.modifyAcl(dir.getPath(), dir.getAcl());
      }
    } catch (IOException e) {
      throw new AuthorizableGatewayException("Can't sync hdfs", e);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

}
