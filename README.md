# OpertusMundi Admin Application

[![Build Status](https://ci.dev-1.opertusmundi.eu:9443/api/badges/OpertusMundi/admin-gateway-service/status.svg?ref=refs/heads/master)](https://ci.dev-1.opertusmundi.eu:9443/OpertusMundi/admin-gateway-service)

Admin application for OpertusMundi marketplace

## Quickstart

### Configure the Web Application

Copy configuration example files from `admin-gateway/config-example/` into `admin-gateway/src/main/resources/`, and edit to adjust to your needs.

`cp -r admin-gateway/config-example/* admin-gateway/src/main/resources/`

### Database configuration

Set the database configuration properties for all profile configuration files.

* application-development.properties
* application-production.properties

```properties
#
# Data source
#

spring.datasource.url = jdbc:postgresql://localhost:5432/opertus-mundi
spring.datasource.username = username
spring.datasource.password = password
spring.datasource.driver-class-name = org.postgresql.Driver

#
# Logging with a log4j2 JDBC appender
#

opertus-mundi.logging.jdbc.url = jdbc:postgresql://localhost:5432/opertus-mundi
opertus-mundi.logging.jdbc.username = username
opertus-mundi.logging.jdbc.password = password
```

* application-testing.properties

```properties
#
# Data source
#

spring.datasource.url = jdbc:postgresql://localhost:5432/opertus-mundi-test
spring.datasource.username = username
spring.datasource.password = password
spring.datasource.driver-class-name = org.postgresql.Driver
```

### Configure default administrator

On startup, the application will create a new administrator account if no other account already exists.
The default account can be configured using the following properties:

```properties
#
# Security
#

opertus-mundi.default-admin.username =
opertus-mundi.default-admin.password =
opertus-mundi.default-admin.firstName=
opertus-mundi.default-admin.lastName =
```

### Configure Feign clients

Admin application connects to several services using Feign clients. The following properties must be set:

```properties
#
# Spring Cloud Feign clients
#

# Global secret for signing JWT tokens shared by all services
opertusmundi.feign.jwt.secret=

# Catalogue service (no authentication)
opertusmundi.feign.catalogue.url=

# BPM server (basic authentication)
opertusmundi.feign.bpm-server.url=
opertusmundi.feign.bpm-server.basic-auth.username=
opertusmundi.feign.bpm-server.basic-auth.password=

# Rating service (basic authentication)
opertusmundi.feign.rating-service.url=
opertusmundi.feign.rating-service.basic-auth.username=
opertusmundi.feign.rating-service.basic-auth.password=

# Email service (JWT token authentication)
# Uses private/public key pair for signing/parsing tokens.
opertusmundi.feign.email-service.url=

# Message service (JWT token authentication)
# Uses opertusmundi.feign.jwt.secret for signing tokens.
opertusmundi.feign.message-service.url=

# Ingest service
opertusmundi.feign.ingest.url=

# Transform service
opertusmundi.feign.transform.url=

# Data Profiler service
opertusmundi.feign.data-profiler.url=

# Persistent Identifier Service
opertusmundi.feign.persistent-identifier-service.url=
```

### Configure file system

Admin application requires access to asset repository and user file system. The following directories must be accessible to the application:

```properties
#
# File system
#

# Folder for creating temporary files
opertusmundi.file-system.temp-dir=
# Root folder for storing user file system
opertusmundi.file-system.data-dir=
# Root folder for storing files for draft assets
opertusmundi.file-system.draft-dir=
# Root folder for storing files for assets
opertusmundi.file-system.asset-dir=
```

### Configure the Web Client

Details on configuring and running the web client application can be found [here](https://github.com/OpertusMundi/frontend-admin/blob/master/README.md).

### Build

Build the project:

    mvn clean package

### Run as standalone JAR

Run application (with an embedded Tomcat 9.x server) as a standalone application:

    java -jar admin-gateway/target/opertus-mundi-admin-gateway-1.0.0.jar

or using the Spring Boot plugin:

    cd admin-gateway && mvn spring-boot:run

### Run as WAR on a servlet container

Normally a WAR archive can be deployed at any servlet container. The following is only tested on a Tomcat 9.x.

Open `pom.xml` and change packaging type to `war`, in order to produce a WAR archive.

Ensure that the following section is not commented (to avoid packaging an embedded server):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>    
```

Rebuild, and deploy generated `admin-gateway/target/opertus-mundi-admin-gateway-1.0.0.war` on a Tomcat 9.x servlet container.
