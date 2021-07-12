package eu.opertusmundi.admin.web.model.configuration;

import java.util.ArrayList;
import java.util.List;

import eu.opertusmundi.common.model.spatial.CountryEuropeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationDto {

    private BingMapsConfigurationDto     bingMaps;
    private final List<CountryEuropeDto> europeCountries = new ArrayList<CountryEuropeDto>();
    private MapConfigurationDto          map;
    private String                       marketplaceUrl;
    private OsmConfigurationDto          osm;

}
