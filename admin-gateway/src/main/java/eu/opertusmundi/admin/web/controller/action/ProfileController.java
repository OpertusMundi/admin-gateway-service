package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.opertusmundi.common.model.account.helpdesk.HelpdeskProfileCommandDto;
import eu.opertusmundi.common.model.RestResponse;

@RequestMapping(produces = "application/json")
public interface ProfileController {

	@RequestMapping(value = "/action/user/profile", method = RequestMethod.GET)
	RestResponse<?> getProfile();

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = "/action/user/profile", method = RequestMethod.POST)
	RestResponse<?> updateProfile(
		@Valid @RequestBody HelpdeskProfileCommandDto command, BindingResult validationResult
	);

}
