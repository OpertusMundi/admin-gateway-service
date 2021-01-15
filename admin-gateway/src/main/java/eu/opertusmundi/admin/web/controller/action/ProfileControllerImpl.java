package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.ProfileCommandDto;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.model.RestResponse;

@RestController
@Secured({ "ROLE_USER" })
@RequestMapping(produces = "application/json")
public class ProfileControllerImpl extends BaseController implements ProfileController {

	@Autowired
	HelpdeskAccountRepository accountRepository;

	@Override
	public RestResponse<?> getProfile(Authentication authentication) {
		final String username = authentication.getName();

		final AccountDto account = this.accountRepository.findOneByEmail(username).get().toDto();

		if (account == null) {
			return RestResponse.accessDenied();
		}

		return RestResponse.result(account);
	}

    @Override
    public RestResponse<?> updateProfile(ProfileCommandDto command, BindingResult validationResult) {
        command.setId(this.currentUserId());

        if (validationResult.hasErrors()) {
            return RestResponse.invalid(validationResult.getFieldErrors());
        }

        final AccountDto result = this.accountRepository.saveProfile(command);

        return RestResponse.result(result);
    }

}
