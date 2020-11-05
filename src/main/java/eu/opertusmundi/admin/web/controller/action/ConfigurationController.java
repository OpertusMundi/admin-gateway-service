package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.config.MapConfiguration;
import eu.opertusmundi.admin.web.model.dto.configuration.ConfigurationDto;
import eu.opertusmundi.common.model.RestResponse;

@RestController
@Secured({ "ROLE_USER", "ROLE_ADMIN" })
@RequestMapping(produces = "application/json")
public class ConfigurationController extends BaseController {

	@Autowired
	private MapConfiguration mapConfiguration;

	@RequestMapping(value = "/action/configuration", method = RequestMethod.GET)
	public RestResponse<ConfigurationDto> getConfiguration() {
		return RestResponse.result(this.createConfiguration());
	}

	private ConfigurationDto createConfiguration() {
		final ConfigurationDto config = new ConfigurationDto();

		config.setOsm(this.mapConfiguration.getOsm());
		config.setBingMaps(this.mapConfiguration.getBingMaps());
		config.setMap(this.mapConfiguration.getDefaults());

		return config;
	}

}
