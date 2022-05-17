package eu.opertusmundi.admin.web.controller.action;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.admin.web.model.configuration.ConfigurationDto;
import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(produces = "application/json")
public interface ConfigurationController {

    @GetMapping(value = "/action/configuration")
    RestResponse<ConfigurationDto> getConfiguration();

}
