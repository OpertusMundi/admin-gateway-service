package eu.opertusmundi.admin.web.model.dto.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationDto {

    private BingMapsConfigurationDto bingMaps;
    private MapConfigurationDto      map;
    private OsmConfigurationDto      osm;

}
