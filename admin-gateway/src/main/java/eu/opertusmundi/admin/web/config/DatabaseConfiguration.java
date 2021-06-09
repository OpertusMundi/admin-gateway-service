package eu.opertusmundi.admin.web.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = {
        eu.opertusmundi.common.repository._Marker.class,
		eu.opertusmundi.admin.web.repository._Marker.class,
    }
)
@EnableTransactionManagement(mode = AdviceMode.PROXY)
public class DatabaseConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean
    @ConfigurationProperties("opertusmundi.camunda.datasource")
    public DataSourceProperties camundaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource defaultDataSource(DataSourceProperties defaultDataSourceProperties) {
        return defaultDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("opertusmundi.camunda.datasource.hikari")
    public HikariDataSource camundaDataSource(
        @Qualifier("camundaDataSourceProperties") DataSourceProperties camundaDataSourceProperties
    ) {
        return camundaDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
    
}