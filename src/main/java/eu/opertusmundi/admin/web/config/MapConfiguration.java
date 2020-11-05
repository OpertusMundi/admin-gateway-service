package eu.opertusmundi.admin.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import eu.opertusmundi.admin.web.model.dto.configuration.BingMapsConfigurationDto;
import eu.opertusmundi.admin.web.model.dto.configuration.MapConfigurationDto;
import eu.opertusmundi.admin.web.model.dto.configuration.OsmConfigurationDto;
import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:config/map.properties")
@ConfigurationProperties()
@Getter
@Setter
public class MapConfiguration {

    private BingMapsConfigurationDto bingMaps;

    private MapConfigurationDto defaults;

    private OsmConfigurationDto osm;

}
