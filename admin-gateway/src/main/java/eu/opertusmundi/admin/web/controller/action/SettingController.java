package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumService;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.SettingDto;
import eu.opertusmundi.common.model.SettingUpdateCommandDto;

@RequestMapping(produces = "application/json")
public interface SettingController {

    @GetMapping(value = "/action/settings")
    RestResponse<List<SettingDto>> findAll(
        @RequestParam(name = "service", required = false) EnumService service
    );

    @PutMapping(value = "/action/settings")
    BaseResponse update(@RequestBody @Valid SettingUpdateCommandDto command, BindingResult validationResult);

}
