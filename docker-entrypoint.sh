#!/bin/sh
set -u -e -o pipefail

[[ "${DEBUG:-false}" != "false" || "${XTRACE:-false}" != "false" ]] && set -x

function _validate_http_url()
{
    local var_name=$1
    local re="^\(https\|http\)://\([a-z][-a-z0-9]*\)\([.][a-z][-a-z0-9]*\)*\([:][1-9][0-9]\{1,4\}\)\?\(/\|$\)"
    grep -e "${re}" || { echo "${var_name} does not seem like an http(s) URL" 1>&2 && false; }
}

function _validate_database_url()
{
    local var_name=$1
    local re="^jdbc:postgresql://\([a-z][-a-z0-9]*\)\([.][a-z][-a-z0-9]*\)*\([:][1-9][0-9]\{1,4\}\)\?/[a-z][-_a-zA-Z0-9]*$"
    grep -e "${re}" || { echo "${var_name} does not seem like a PostgreSQL JDBC connection URL" 1>&2 && false; }
}

function _get_url_scheme()
{
    echo $1 | sed -E 's#^([a-z]+)://.*#\1#'
}

function _get_url_host()
{
    echo $1 | sed -E 's#^([a-z]+)://([^:\/]*)([:][1-9][0-9]{1,4})?(/.*|$)#\2#'
}

function _get_url_port()
{
    echo $1 | sed -E 's#^([a-z]+)://([^:\/]*)([:]([1-9][0-9]{1,4}))?(/.*|$)#\4#'
}

runtime_profile=$(hostname | md5sum | head -c10)

{
    marketplace_url=$(echo ${MARKETPLACE_URL} | _validate_http_url "MARKETPLACE_URL")
    echo "opertusmundi.marketplace.url = ${marketplace_url}"
    
    admin_username=${ADMIN_USERNAME}
    admin_password=$(cat ${ADMIN_PASSWORD_FILE} | tr -d '\n')
    echo "opertus-mundi.default-admin.username = ${admin_username}"
    echo "opertus-mundi.default-admin.password = ${admin_password}"

    database_url=$(echo ${DATABASE_URL} | _validate_database_url "DATABASE_URL")
    database_username=${DATABASE_USERNAME}
    database_password=$(cat ${DATABASE_PASSWORD_FILE} | tr -d '\n')
    echo "spring.datasource.url = ${database_url}"
    echo "spring.datasource.username = ${database_username}"
    echo "spring.datasource.password = ${database_password}"
    
    camunda_database_url=$(echo ${CAMUNDA_DATABASE_URL} | _validate_database_url "CAMUNDA_DATABASE_URL")
    camunda_database_username=${CAMUNDA_DATABASE_USERNAME}
    camunda_database_password=$(cat ${CAMUNDA_DATABASE_PASSWORD_FILE} | tr -d '\n')
    echo "opertusmundi.camunda.datasource.url = ${camunda_database_url}"
    echo "opertusmundi.camunda.datasource.username = ${camunda_database_username}"
    echo "opertusmundi.camunda.datasource.password = ${camunda_database_password}"

    jwt_secret=$(cat ${JWT_SECRET_FILE} | tr -d '\n')
    echo "opertusmundi.feign.jwt.secret = ${jwt_secret}"

    bpm_rest_base_url=$(echo ${BPM_REST_BASE_URL} | _validate_http_url "BPM_REST_BASE_URL")
    bpm_rest_username=${BPM_REST_USERNAME}
    bpm_rest_password=$(cat ${BPM_REST_PASSWORD_FILE} | tr -d '\n')
    echo "opertusmundi.feign.bpm-server.url = ${bpm_rest_base_url}"
    echo "opertusmundi.feign.bpm-server.basic-auth.username = ${bpm_rest_username}"
    echo "opertusmundi.feign.bpm-server.basic-auth.password = ${bpm_rest_password}"

    mangopay_base_url=$(echo ${MANGOPAY_BASE_URL} | _validate_http_url "MANGOPAY_BASE_URL")
    mangopay_client_id=${MANGOPAY_CLIENT_ID}
    mangopay_client_password=$(cat ${MANGOPAY_CLIENT_PASSWORD_FILE} | tr -d '\n')
    echo "opertusmundi.payments.mangopay.base-url = ${mangopay_base_url}"
    echo "opertusmundi.payments.mangopay.client-id = ${mangopay_client_id}"
    echo "opertusmundi.payments.mangopay.client-password = ${mangopay_client_password}"

    catalogue_base_url=$(echo ${CATALOGUE_BASE_URL} | _validate_http_url "CATALOGUE_BASE_URL")
    echo "opertusmundi.feign.catalogue.url = ${catalogue_base_url}"

    ingest_base_url=$(echo ${INGEST_BASE_URL} | _validate_http_url "INGEST_BASE_URL")
    echo "opertusmundi.feign.ingest.url = ${ingest_base_url}"

    transform_base_url=$(echo ${TRANSFORM_BASE_URL} | _validate_http_url "TRANSFORM_BASE_URL")
    echo "opertusmundi.feign.transform.url = ${transform_base_url}"

    mailer_base_url=$(echo ${MAILER_BASE_URL} | _validate_http_url "MAILER_BASE_URL")
    echo "opertusmundi.feign.email-service.url = ${mailer_base_url}"
    echo "opertusmundi.feign.email-service.jwt.subject = api-gateway"

    messenger_base_url=$(echo ${MESSENGER_BASE_URL} | _validate_http_url "MESSENGER_BASE_URL")
    echo "opertusmundi.feign.message-service.url = ${messenger_base_url}"
    echo "opertusmundi.feign.message-service.jwt.subject = api-gateway"

    rating_base_url=$(test -z "${RATING_BASE_URL:-}" && echo -n || \
        { echo ${RATING_BASE_URL} | _validate_http_url "RATING_BASE_URL"; })
    rating_username=${RATING_USERNAME}
    rating_password=$({ test -f "${RATING_PASSWORD_FILE}" && cat ${RATING_PASSWORD_FILE}; } | tr -d '\n' || echo -n)
    echo "opertusmundi.feign.rating-service.url = ${rating_base_url}"
    echo "opertusmundi.feign.rating-service.basic-auth.username = ${rating_username}"
    echo "opertusmundi.feign.rating-service.basic-auth.password = ${rating_password}"

    profile_base_url=$(echo ${PROFILE_BASE_URL} | _validate_http_url "PROFILE_BASE_URL")
    echo "opertusmundi.feign.data-profiler.url = ${profile_base_url}"
    
    pid_base_url=$(echo ${PID_BASE_URL} | _validate_http_url "PID_BASE_URL")
    echo "opertusmundi.feign.persistent-identifier-service.url= ${pid_base_url}"

    elasticsearch_base_url=$(echo ${ELASTICSEARCH_BASE_URL%/} | _validate_http_url "ELASTICSEARCH_BASE_URL")
    elasticsearch_indices_assets_index_name=${ELASTICSEARCH_INDICES_ASSETS_INDEX_NAME}
    echo "spring.elasticsearch.rest.uris = ${elasticsearch_base_url}"
    echo "opertusmundi.elastic.asset-index.name = ${elasticsearch_indices_assets_index_name}"

    if [ -n "${RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_BASE_URL}" ]; then
        rsyslog_log_aggregation_elasticsearch_base_url=$(echo ${RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_BASE_URL} | \
            _validate_http_url "RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_BASE_URL")
        rsyslog_log_aggregation_elasticsearch_index_name=${RSYSLOG_LOG_AGGREGATION_ELASTICSEARCH_INDEX_NAME}
        echo "opertusmundi.logging.elastic.hosts[0].hostname = $(_get_url_host ${rsyslog_log_aggregation_elasticsearch_base_url})"
        echo "opertusmundi.logging.elastic.hosts[0].port = $(_get_url_port ${rsyslog_log_aggregation_elasticsearch_base_url})"
        echo "opertusmundi.logging.elastic.hosts[0].scheme = $(_get_url_scheme ${rsyslog_log_aggregation_elasticsearch_base_url})"
        echo "opertusmundi.logging.elastic.rsyslog-index.name = ${rsyslog_log_aggregation_elasticsearch_index_name}"
    fi
    
} > ./config/application-${runtime_profile}.properties

logging_config="classpath:config/log4j2.xml"
if [[ -f "./config/log4j2.xml" ]]; then
    logging_config="file:config/log4j2.xml"
fi

# Run

main_class=eu.opertusmundi.admin.web.Application
default_java_opts="-server -Djava.security.egd=file:///dev/urandom -Xms256m"
exec java ${JAVA_OPTS:-${default_java_opts}} -cp "/app/classes:/app/dependency/*" ${main_class} \
  --spring.profiles.active=production,${runtime_profile} --logging.config=${logging_config}

