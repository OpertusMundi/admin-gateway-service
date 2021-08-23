package eu.opertusmundi.admin.web.controller.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.config.MapConfiguration;
import eu.opertusmundi.admin.web.model.configuration.ConfigurationDto;
import eu.opertusmundi.common.domain.CountryEuropeEntity;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.model.contract.EnumIcon;
import eu.opertusmundi.common.repository.CountryRepository;

@RestController
public class ConfigurationControllerImpl extends BaseController implements ConfigurationController {

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
        config.setIcons(this.getIcons());

        this.countryRepository.getEuropeCountries().stream()
            .map(CountryEuropeEntity::toDto)
            .forEach(c -> config.getEuropeCountries().add(c));

        return config;
    }
    

    private Map<EnumIcon, byte[]> getIcons() {
    	Map<EnumIcon, byte[]> icons = new HashMap<EnumIcon, byte[]>();
    	for (EnumIcon icon : EnumIcon.values()) {
        	InputStream fileStream = null;
			try {
				fileStream = resourceLoader.getResource(iconFolder + icon.getFile()).getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	byte[] data = null;
			try {
				data = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		icons.put(icon, data);
    	}
    	
    	return icons;
    }

}
