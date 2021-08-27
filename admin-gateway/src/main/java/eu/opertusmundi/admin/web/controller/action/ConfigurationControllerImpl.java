package eu.opertusmundi.admin.web.controller.action;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.config.MapConfiguration;
import eu.opertusmundi.admin.web.model.configuration.ConfigurationDto;
import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.common.domain.CountryEuropeEntity;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.model.contract.ContractIconDto;
import eu.opertusmundi.common.model.contract.EnumIcon;
import eu.opertusmundi.common.repository.CountryRepository;

@RestController
public class ConfigurationControllerImpl extends BaseController implements ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Value("${opertusmundi.marketplace.url}")
    private String marketplaceUrl;

    @Value("${opertusmundi.contract.icons}")
    private String iconFolder;

    @Autowired
    private MapConfiguration mapConfiguration;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ResourceLoader resourceLoader;

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
            .forEach(config.getEuropeCountries()::add);

        this.setIcons(config);

        return config;
    }

    private void setIcons(ConfigurationDto config) {
        for (EnumIcon icon : EnumIcon.values()) {
            final Path path = Paths.get(iconFolder, icon.getFile());
            try (final InputStream fileStream = resourceLoader.getResource(path.toString()).getInputStream()) {
                final byte[] data = IOUtils.toByteArray(fileStream);;
                config.getContractIcons().add(ContractIconDto.of(icon, icon.getCategory(), data));
            } catch (IOException ex) {
                logger.error(String.format("Failed to load resource [icon=%s, path=%s]", icon, path), ex);
            }
        }
    }

}
