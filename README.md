#Auth-gateway
A gateway for adding and removing authorization data for hadoop components.

## General description

When adding a new organization or a new user to TAP platform, there are many steps to perform. Reason of creating Auth-gateway is to keep one point that should be called during manipulations over collections of orgs and users. Auth-gateway will make calls to all Hadoop components in order to keep authorization information in sync. Auth-gateway is accessible via REST API and exposes 5 operations.

* Add organization
* Delete organization
* Add user to organization
* Remove user from organization
* Synchronize organizations and users
* Get organizations and users synchronize state

![](wikiimages/auth-gateway.png)

Because of different authorization methods and communication protocols of Hadoop elements, Auth-gateway has two types of components.

* **Auth-gateway providers** - responsible for performing changes in authorization structure in one Hadoop element, for instance: changing Access Control Lists in Zookeeper.
* **Auth-gateway engine** - responsible for invoking gateway providers in parallel manner.

### Gateway engine description

Gateway-engine uses CompletableFuture to handle parallel invocation of providers and timeouts. It utilize some kind of barrier to wait on end of execution of all providers. If any provider fails, engine logs and returns an error. Error message is proxied to the client.

### General configuration for engine
To achieve synchronization mechanism, auth-gateway requires:

* Management service for retrieving organizations list and users state. Currently auth-gateway uses [User-management](https://github.com/intel-data/user-management) project. Environment variables:
  * obligatory
    * SSO_TOKENKEY - used by client to verify that a token came from the UAA
    * SSO_CLIENTID - client id
    * SSO_CLIENTSECRET - client secret
    * SSO_UAAURI - UAA management service uri
    * SSO_APIENDPOINT - UAA management service api uri
* Zookeeper quorum to store users and organizations state. Environment variables:
  * obligatory
    * ZK_USER - user used to secure state zNode.
    * ZK_PASSWORD - password for ZK_USER, used to secure state zNode 
    * ZK_CLUSTER_URL - quorum address (host:2181,host:2181,host:2181)
    * KRB_ENABLED - determine mode of authentication: false - simply, true - kerberos - in this case environment variables listed below are obligatory.
  * kerberos 
    * KRB_KDC - key distribution center address
    * KRB_REALM - kerberos realm name
    * KRB_USER - auth-gateway principal name
    * KRB_KEYTAB - auth-gateway principal keytab for KRB_USER

### Providers description

#### General configuration for all providers
Every provider should implement two authentication approach with Hadoop cluster: simple and kerberos. Currently to achieve this goal, every provider could be run under one of two spring profiles. Base kerberos configuration should be present on file system (krb5.conf) or should be used based on environment variables described at previous section.

#### HDFS provider
---
HDFS provider is an optional part of the gateway, which is responsible for creating several directories on HDFS like:

* /h2o - home directory for H2O
* /user/h2o - home directory for h2o user
* /org/org_name/ - organization directory - permissions set for user org_admin
* /org/org_name/shared - shared directory - permissions set for group org
* /org/org_name/broker - broker directory - permissions set for user org_admin
* /org/org_name/apps/ - applications directory - permislsions set for user org
* /org/org_name/users/ - users directories - permissions set for user org_admin
* /org/org_name/users/user_name - directory for each user in organization - permissions set for user

##### Configuration
Environment variables list:

* HDFS_CONFIG_PATH (required) - path to directory of HDFS client configuration files (hdfs-site.xml, core-site.xml).

Spring profiles:

* krb-hdfs-auth-gateway - kerberos authentication
* hdfs-auth-gateway - simple authentication

HDFS module is configured based on properties file which allow user to define three kinds of directories:

* base directories, not related with organization like /user/<user_name>
* organization root directory
* organization users directory
* all organizations subdirectories
  
Each directory has configurable base properties like: path, permissions, owner, group and list of acls. 
```
hdfs:
  structure:
    base:
      - path: /user/broker
        permissions: drwx------
        user: broker
        group: broker
    org:
      root:
        path: /org/{org}
        permissions: drwxrwx---
        acl:
          - user:broker:--x
          - group:hive:r-x
      user:
        path: /org/{org}/user/{user}
        permissions: drwx------
	dirs:
   	  - path: jars
	    permissions: drwxrwx---
	    acl:
	      - user:broker:rwx
```

#### Group Mapping Provider
---
Group Mapping provider is an optional part of the gateway, which is responsible for creating groups and users in Hadoop. This provider communicate with [Hadoop Group Mapping Service](https://github.com/intel-data/hadoop-groups-mapping-service).

Spring profiles:

* krb-hgm-auth-gateway - kerberos authentication
* hgm-auth-gateway - simple authentication

Environment variables list:

* obligatory
  * HGM_URL - Hadoop Group Mapping Service address
* kerberos mode requires providing keytab file and principal configured for Hadoop Group Mapping Service.
  * HGM_PRINCIPAL - principal used to authenticate against Kerberos
  * HGM_PRINCIPAL_KEYTAB_PATH - path to keytab with credentials for HGM_PRINCIPAL
* simple, When group mapping running without kerberos it will use HTTPS to communicate with Hadoop Group Mapping service, so instead of principal and his keytab, you need to provide credentials.
  * HGM_USERNAME - username used to authenticate 
  * HGM_PASSWORD - password for HGM_USERNAME

#### Zookeeper provider

Zookeeper provider works differently in kerberos and non-kerberos environment.

* **Without kerberos**

  It creates znodes (one per single organization) but it didn't set any ACLs. Kerberos-less environment is unrecommended if you are aiming for security.

* **With kerberos**

  It creates znodes (one per single organization) just like on the non-kerberos environment, but it also secure it with ACLs. Only super-user has access to newly created userless organization. During adding user to organization, zookeeper provider will add this user to ACL of his organization znode.

  ACLs used by zookeeper provider are based on SASL authentication scheme.
  
Spring profiles:

* krb-zookeeper-auth-gateway - kerberos authentication
* zookeeper-auth-gateway - simple authentication
  
#### Warehouse provider
Warehouse provider is responsible for managing sentry roles (Role-Base Administration) and Hive databases. In actual implementation (for cross-organizations isolation) one sentry role is created for every new organization, as well as database in Hive. Created role is granted to groups: org, org_admin.

Spring profiles:

* krb-warehouse-auth-gateway - kerberos authentication
* warehouse-auth-gateway - simple authentication

Environment variables list:

* obligatory
  * SENTRY_ADDRESS: address where sentry service listen on
  * SENTRY_PORT: port where sentry service listen on (default: 8038)
  * IMPALA_AVAILABLE: determine availability of impala
  * HIVE_CONNECTIONURL: jdbc connection string for hive
  * WAREHOUSE_SUPERUSER: principal with administrator permissions in Sentry and Hive
  * SENTRY_PRINCIPAL: service principal name defined for sentry service (default: sentry)
  * HIVE_CONFIG_PATH: path to directory of HIVE client configuration files (core-site.xml, hive-site.xml, mapred-site.xml).
* impala  
  * IMPALA_CONNECTIONURL: optional connection string for Impala, only used with IMPALA_AVAILABLE
* kerberos
  * WAREHOUSE_KEYTAB_PATH: path to keytab with credentials for HGM_SUPERUSER

#### HBase provider
HBase provider is responsible for namespace creation with permissions for particular group. 

Spring profiles:

* krb-hbase-auth-gateway - kerberos authentication
* hbase-auth-gateway - simple authentication

Environment variables list:

* HBASE_CONFIG_PATH: path to directory of HBASE client configuration files (core-site.xml, hbase-site.xml, hdfs-site.xml).

Requirements:

* KRB_USER has to be in group of Hbase admins.

#### Yarn provider
Yarn provider is responsible for creation Yarn queue with ACL for particular group. Currently this provider is able to work only with Cloudera distribution of Hadoop, because of using [Dynamic Resource Pools](https://www.cloudera.com/documentation/enterprise/5-8-x/topics/cm_mc_resource_pools.html). Implementation communicate with Cloudera Manager by [REST API](https://cloudera.github.io/cm_api/apidocs/v14/rest.html) to update current Dynamic Resource Pools. The root queue is configured with [FAIR Scheduler](https://hadoop.apache.org/docs/r2.7.1/hadoop-yarn/hadoop-yarn-site/FairScheduler.html) by default.

Spring profiles:

* krb-yarn-auth-gateway - kerberos authentication
* yarn-auth-gateway - simple authentication

Environment variables list:

* obligatory
  * CLOUDERA_ADDRESS: Cloudera Manager host address
  * CLOUDERA_USER: admin user for Cloudera Manager
  * CLOUDERA_PASSWORD: password for CLOUDERA_USER

## Configuration and deployment

##### Build 
```mvn clean package```

##### Docker image generation
After build, from ```auth-gateway-engine``` directory:
```mvn docker:build```
  
### Examples 
  
* Example kubernetes deployment: [a link](https://github.com/intel-data/auth-gateway/blob/master/auth-gateway-engine/src/main/resources/application.yml)
 
## Calling Auth-gateway with REST API

* Create organization

  Path: ```/organizations/{orgID}?orgName={orgName}```

  Method: PUT

* Delete organization

  Path: ```/organizations/{orgID}?orgName={orgName}```

  Method: DELETE

* Add user to organization

  Path: ```/organizations/{orgID}/users/{userID}```

  Method: PUT

* Remove user from organization

  Path: ```/organizations/{orgID}/users/{userID}```

  Method: DELETE

* Recreate all organizations and users - fix platform after unsuccessful organization creation, upgrade CDH structure

  Path: ```/synchronize```

  Method: PUT

* Recreate organization - fix platform after unsuccessful organization creation

  Path: ```/synchronize/organizations/{orgID}```

  Method: PUT

* Recreate user in organization - fix platform after unsuccessful user creation

  Path: ```/synchronize/organizations/{orgID}/users/{userID}```

  Method: PUT

* Get synchronize status of all organizations

  Path: ```/state```

  Method: GET

* Get synchronize status of organization

  Path: ```/state/organizations/{orgID}```

  Method: GET

* Get synchronize status of user in organization

  Path: ```/state/organizations/{orgID}/users/{userID}```

  Method: GET


* Get job status. Running REST API in background mode is possible by adding 'async=true' to query

  Path: ```/jobs/{jobID}```

  Method: GET

## Development

Testing:
```mvn clean test```

Building executable jar:
```mvn clean package```

Building docker image, from ```auth-gateway-engine``` directory:
```mvn docker:build```

### Creating new provider

* Create new sub-project. Call it ```<component>-auth-gateway``` where ```<component>``` is an element of hadoop you want to be called by auth-engine.
* Add this sub-project to modules section in pom.xml.
* Implement ```org.trustedanalytics.auth.gateway.spi.Authorizable``` interface.
* Create configuration class. It should be annotated with ```org.springframework.context.annotation.Configuration``` and be placed in ```org.trustedanalytics.auth.gateway.*``` package. It is also recommended to use ```org.springframework.context.annotation.Profile``` annotation with ```<component>-auth-gateway``` as argument.
* In configuration class place Bean factory method annotated with ```org.springframework.context.annotation.Bean```. This method should return your Authorizable implementation ready to use.
* In auth-gateway-engine add dependency to your sub-project.
* At least you should add ```krb-<component>-auth-gateway``` or ```<component>-auth-gateway``` to comma-separated lists of active Spring profiles: ```SPRING_PROFILES_ACTIVE```.

