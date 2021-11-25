package eu.opertusmundi.admin.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import eu.opertusmundi.common.model.catalogue.elastic.IndexDefinition;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "opertusmundi.logging.elastic")
public class LoggingElasticConfiguration {

    @Getter
    @Setter
    private IndexDefinition rsyslogIndex;

    @Getter
    @Setter
    private HttpHostConfig[] hosts;

}