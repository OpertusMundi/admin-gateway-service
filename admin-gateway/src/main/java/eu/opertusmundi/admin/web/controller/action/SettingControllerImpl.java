package eu.opertusmundi.admin.web.controller.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumService;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.SettingDto;
import eu.opertusmundi.common.model.SettingUpdateCommandDto;
import eu.opertusmundi.common.repository.SettingRepository;

@RestController
@Secured({"ROLE_ADMIN"})
public class SettingControllerImpl extends BaseController implements SettingController {

    private SettingRepository settingRepository;

    @Autowired
    public SettingControllerImpl(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public RestResponse<List<SettingDto>> findAll(EnumService service) {
        final List<SettingDto> settings = service == null
            ? this.settingRepository.findAllAsObjects()
            : this.settingRepository.findAllByServiceAsObjects(service);
                
        return RestResponse.result(settings);
    }

    @Override
    public BaseResponse update(SettingUpdateCommandDto command, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            return RestResponse.invalid(validationResult.getFieldErrors(), validationResult.getGlobalErrors());
        }
        command.setUserId(this.currentUserId());

        this.settingRepository.update(command);

        return BaseResponse.empty();
    }
    
}
