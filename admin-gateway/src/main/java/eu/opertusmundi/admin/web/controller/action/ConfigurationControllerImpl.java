package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.config.MapConfiguration;
import eu.opertusmundi.admin.web.model.configuration.ConfigurationDto;
import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.common.domain.CountryEuropeEntity;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.repository.CountryRepository;

@RestController
public class ConfigurationControllerImpl extends BaseController implements ConfigurationController {

    @Value("${opertusmundi.marketplace.url}")
    private String marketplaceUrl;

    @Autowired
    private MapConfiguration mapConfiguration;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BpmEngineService bpmEngineService;

    public RestResponse<ConfigurationDto> getConfiguration() {
        if (!this.hasRole(EnumHelpdeskRole.USER)) {
            return RestResponse.accessDenied();
        }
        return RestResponse.result(this.createConfiguration());
    }

    private ConfigurationDto createConfiguration() {
        final ConfigurationDto config = new ConfigurationDto();

        config.setOsm(this.mapConfiguration.getOsm());
        config.setBingMaps(this.mapConfiguration.getBingMaps());
        config.setMap(this.mapConfiguration.getDefaults());
        config.setMarketplaceUrl(marketplaceUrl);
        config.setProcessDefinitions(this.bpmEngineService.getProcessDefinitions());

        this.countryRepository.getEuropeCountries().stream()
            .map(CountryEuropeEntity::toDto)
            .forEach(c -> config.getEuropeCountries().add(c));

        return config;
    }

}
