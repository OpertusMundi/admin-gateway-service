package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskProfileCommandDto;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.model.RestResponse;

@RestController
public class ProfileControllerImpl extends BaseController implements ProfileController {

	@Autowired
	HelpdeskAccountRepository accountRepository;

	@Override
	public RestResponse<?> getProfile() {
        if (!this.hasRole(EnumHelpdeskRole.USER)) {
            return RestResponse.accessDenied();
        }

		final HelpdeskAccountDto account = this.accountRepository.findOneByEmail(this.currentUserName()).get().toDto();

		if (account == null) {
			return RestResponse.accessDenied();
		}

		return RestResponse.result(account);
	}

    @Override
    public RestResponse<?> updateProfile(HelpdeskProfileCommandDto command, BindingResult validationResult) {
        command.setId(this.currentUserId());

        if (validationResult.hasErrors()) {
            return RestResponse.invalid(validationResult.getFieldErrors());
        }

        final HelpdeskAccountDto result = this.accountRepository.saveProfile(command);

        return RestResponse.result(result);
    }

}
