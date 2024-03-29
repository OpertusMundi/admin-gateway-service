# vim: set syntax=dockerfile:

FROM node:14.17.5-buster AS npm-build

ENV NPM_CONFIG_PROGRESS="false" NPM_CONFIG_SPIN="false"
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

WORKDIR /app

COPY admin-gateway/src/main/frontend/ /app/
RUN npm install && npm run build


#FROM maven:3.8.6-eclipse-temurin-17-alpine as maven-build
# see https://github.com/OpertusMundi/java-commons/blob/master/Dockerfile
FROM opertusmundi/java-commons-builder:1.1 as maven-build

WORKDIR /app

COPY common /app/common/
RUN (cd /app/common && mvn -B install)

COPY pom.xml /app/
COPY admin-gateway/pom.xml /app/admin-gateway/
RUN mvn -B dependency:resolve-plugins dependency:resolve
RUN mvn -B -pl admin-gateway dependency:copy-dependencies -DincludeScope=runtime

COPY admin-gateway/src/main/resources /app/admin-gateway/src/main/resources
COPY admin-gateway/src/main/java /app/admin-gateway/src/main/java
COPY admin-gateway/resources /app/admin-gateway/resources
COPY --from=npm-build /app/build /app/admin-gateway/src/main/frontend/build
RUN mvn -B compile -DenableJavaBuildProfile -DenableDockerBuildProfile


FROM eclipse-temurin:17-jre-alpine 

ARG git_commit=

COPY --from=maven-build /app/admin-gateway/target/ /app/

RUN addgroup spring && adduser -H -D -G spring spring

COPY docker-entrypoint.sh /usr/local/bin
RUN chmod a+x /usr/local/bin/docker-entrypoint.sh

WORKDIR /app

RUN mkdir config logs \
    && chgrp spring config logs \
    && chmod g=rwx config logs

ENV MARKETPLACE_URL="" \
    ADMIN_USERNAME="admin@example.com" \
    ADMIN_PASSWORD_FILE="/secrets/admin-password" \
    DATABASE_URL="jdbc:postgresql://db:5432/opertusmundi" \
    DATABASE_USERNAME="spring" \
    DATABASE_PASSWORD_FILE="/secrets/database-password" \
    CAMUNDA_DATABASE_URL="jdbc:postgresql://db:5432/opertusmundi-camunda" \
    CAMUNDA_DATABASE_USERNAME="opertusmundi-camunda-reader" \
    CAMUNDA_DATABASE_PASSWORD_FILE="/secrets/camunda-database-password" \
    JWT_SECRET_FILE="/secrets/jwt-signing-key" \
    OIDC_CLIENT_ID="admin-gateway" \
    OIDC_CLIENT_SECRET_FILE="/secrets/openid-client-secret" \
    OIDC_SCOPE="openid,email,profile,roles" \
    OIDC_AUTH_URL="" \
    OIDC_TOKEN_URL="" \
    OIDC_USERINFO_URL="" \
    OIDC_JWKS_URL="" \
    BPM_REST_BASE_URL="http://bpm-server:8000/engine-rest" \
    BPM_REST_USERNAME="" \
    BPM_REST_PASSWORD_FILE="/secrets/bpm-rest-password" \
    MANGOPAY_BASE_URL="https://api.mangopay.com" \
    MANGOPAY_CLIENT_ID="" \
    MANGOPAY_CLIENT_PASSWORD_FILE="/secrets/mangopay-client-password" \
    CATALOGUE_BASE_URL="http://catalogueapi:8000/" \
    INGEST_BASE_URL="http://ingest:8000/" \
    TRANSFORM_BASE_URL="http://transform:8000/" \
    MAILER_BASE_URL="http://mailer:8000/" \
    MESSENGER_BASE_URL="http://messenger:8000/" \
    PROFILE_BASE_URL="http://profile:8000/" \
    PID_BASE_URL="http://pid:8000/" \
    ELASTICSEARCH_BASE_URL="http://elasticsearch:9200" \
    ELASTICSEARCH_INDICES_ASSETS_INDEX_NAME="assets" \
    RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_BASE_URL="http://elasticsearch:9200" \
    RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_INDEX_NAME="rsyslog_local7" \
    KEYCLOAK_URL="" \
    KEYCLOAK_REALM="Topio-Market" \
    KEYCLOAK_REFRESH_TOKEN_FILE=""


ENV GIT_COMMIT=${git_commit}

VOLUME [ \
    "/var/local/opertusmundi/files/assets", \
    "/var/local/opertusmundi/files/users", \
    "/var/local/opertusmundi/files/drafts", \
    "/var/local/opertusmundi/files/temp", \
    "/var/local/opertusmundi/ingest/input" ]

USER spring
ENTRYPOINT [ "/usr/local/bin/docker-entrypoint.sh" ]

