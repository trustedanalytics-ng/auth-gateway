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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HdfsClient.class);

  private final FileSystem fileSystem;

  private HdfsClient(FileSystem fs) {
    this.fileSystem = fs;
  }

  /**
   * Creates new HdfsClient instance
   * 
   * @param fs FileSystem object which will be used for all Hdfs operations
   * @return new instance of HdfsClient
   */
  public static HdfsClient getInstance(FileSystem fs) {
    return new HdfsClient(fs);
  }

  /**
   * If directory not exists method creates new directory with proper permissions defined in
   * Directory object. If directory exsits method only updates permissions. In both case ACL will be
   * updated.
   * 
   * @param path Path of created directory
   * @param permissions Directory permissions
   * @param user Owner of directory
   * @param group Group of directory
   * @throws IOException
   */
  public void create(Path path, FsPermission permissions, String user, String group)
      throws IOException {
    if (!fileSystem.exists(path)) {
      fileSystem.mkdirs(path);
      fileSystem.setPermission(path, permissions);
      fileSystem.setOwner(path, user, group);
    } else {
      LOGGER.warn(String.format("Path already exists: %s", path));
      modifyPermissions(path, group, permissions);
    }
  }

  /**
   * Modify acl for directory or file. File that include ACL with scope Default will be ignored
   * 
   * @param path File or Directory path on Hdfs.
   * @param aclEntries List of Entries, each entry represents one ACL.
   * @throws IOException
   */
  public void modifyAcl(Path path, List<AclEntry> aclEntries) throws IOException {
    if (fileSystem.isDirectory(path)) {
      fileSystem.modifyAclEntries(path, aclEntries);
    } else {
      fileSystem.modifyAclEntries(path, aclEntries.stream()
          .filter(acl -> acl.getScope() != AclEntryScope.DEFAULT).collect(Collectors.toList()));
    }
  }

  /**
   * Modify acl for directory or file, also all files and subdirectories.
   *
   * @param path
   * @param aclEntries
   * @throws IOException
   */
  public void modifyAclRecursively(Path path, List<AclEntry> aclEntries) throws IOException {
    List<FileStatus> children = getAllChildens(path, true);

    for (FileStatus child : children) {
      modifyAcl(child.getPath(), aclEntries);
    }
  }

  /**
   * Remove file of directory on Hdfs.
   * 
   * @param path File or Directory path on Hdfs.
   * @throws IOException
   */
  public void remove(Path path) throws IOException {
    if (fileSystem.exists(path)) {
      fileSystem.delete(path, true);
    } else {
      LOGGER.warn(String.format("Directory or file under: %s not exists.", path));
    }
  }

  /**
   * Change current root.
   * 
   * @param path All relatives path will be resolved from this.
   * @throws IOException
   */
  public void setWorkingDir(Path path) throws IOException {
    this.fileSystem.setWorkingDirectory(path);
  }

  /**
   * Modify permissions of file/directory under provided path.
   * 
   * @param path Path to file/directory
   * @param group New group
   * @param permission Set of permissions
   * @throws IOException
   */
  public void modifyPermissions(Path path, String group, FsPermission permission)
      throws IOException {
    FileStatus status = fileSystem.getFileStatus(path);
    FsPermission fsPermission = status.getPermission();

    FsAction userAction = fsPermission.getUserAction().or(permission.getUserAction());
    FsAction groupAction = fsPermission.getGroupAction().or(permission.getGroupAction());
    FsAction otherAction = fsPermission.getOtherAction().or(permission.getOtherAction());
    fsPermission = new FsPermission(userAction, groupAction, otherAction);

    fileSystem.setPermission(path, fsPermission);
    fileSystem.setOwner(path, status.getOwner(), group);
  }

  /**
   * Modify permissions of file/directory and also all files and subdirectories.
   * 
   * @param path Path to file/directory
   * @param group New group
   * @param permission Set of permissions
   * @throws IOException
   */
  public void modifyPermissionsRecursively(Path path, String group, FsPermission permission)
      throws IOException {
    List<FileStatus> children = getAllChildens(path, true);

    for (FileStatus child : children) {
      modifyPermissions(child.getPath(), group, permission);
    }
  }

  /**
   * Get list of children for path.
   * @param path Path to directory
   * @param recursive Optional recursive parameter
   * @return
   * @throws IOException
   */
  private List<FileStatus> getAllChildens(Path path, boolean recursive) throws IOException {
    List<FileStatus> files = new ArrayList<>();
    FileStatus[] statuses = fileSystem.listStatus(path);

    for (FileStatus status : statuses) {
      files.add(status);
      if (status.isDirectory() && recursive)
        files.addAll(getAllChildens(status.getPath(), recursive));
    }

    return files;
  }
}
