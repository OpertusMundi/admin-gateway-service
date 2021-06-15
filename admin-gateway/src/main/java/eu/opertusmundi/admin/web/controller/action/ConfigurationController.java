package eu.opertusmundi.admin.web.controller.action;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.opertusmundi.admin.web.model.configuration.ConfigurationDto;
import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(produces = "application/json")
public interface ConfigurationController {

    @RequestMapping(value = "/action/configuration", method = RequestMethod.GET)
    RestResponse<ConfigurationDto> getConfiguration();

}
