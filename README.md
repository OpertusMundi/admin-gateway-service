# OpertusMundi HelpDesk Application

HelpDesk application for OpertusMundi marketplace

## Quickstart

### Configure the Web Application

Copy configuration example files from `config-example/` into `src/main/resources/`, and edit to adjust to your needs.

`cp -r config-example/* src/main/resources/`

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
opertus-mundi.default-admin.firstname=
opertus-mundi.default-admin.lastname =
```

### Configure the Web Client

Details on configuring and running the web client application can be found [here](src/main/frontend/README.md).

### Build

Build the project:

    mvn clean package

### Run as standalone JAR

Run application (with an embedded Tomcat 9.x server) as a standalone application:

    java -jar target/opertus-mundi-helpdesk-app-1.0.0.jar

or using the Spring Boot plugin:

    mvn spring-boot:run

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

Rebuild, and deploy generated `target/opertus-mundi-helpdesk-app-1.0.0.war` on a Tomcat 9.x servlet container.