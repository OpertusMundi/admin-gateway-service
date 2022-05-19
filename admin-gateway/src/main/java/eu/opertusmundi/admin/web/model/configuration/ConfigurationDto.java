package eu.opertusmundi.admin.web.model.configuration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.opertusmundi.admin.web.model.workflow.ProcessDefinitionHeaderDto;
import eu.opertusmundi.common.model.EnumAuthProvider;
import eu.opertusmundi.common.model.contract.ContractIconDto;
import eu.opertusmundi.common.model.integration.ExternalDataProviderDto;
import eu.opertusmundi.common.model.spatial.CountryEuropeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationDto {

    private List<EnumAuthProvider> authProviders;
    
    @JsonInclude(Include.NON_NULL)
    private BingMapsConfigurationDto bingMaps;

    private String clientId;
    
    @JsonInclude(Include.NON_EMPTY)
    private final List<ContractIconDto> contractIcons = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private final List<CountryEuropeDto> europeCountries = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private final List<ExternalDataProviderDto> externalProviders = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private MapConfigurationDto map;

    private String marketplaceUrl;

    @JsonInclude(Include.NON_NULL)
    private OsmConfigurationDto osm;

    @JsonInclude(Include.NON_EMPTY)
    private List<ProcessDefinitionHeaderDto> processDefinitions;

}
