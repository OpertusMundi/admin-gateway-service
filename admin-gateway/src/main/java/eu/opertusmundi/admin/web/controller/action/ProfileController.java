package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskProfileCommandDto;

@RequestMapping(produces = "application/json")
public interface ProfileController {

	@GetMapping(value = "/action/user/profile")
	RestResponse<?> getProfile();

	@Secured({ "ROLE_USER" })
	@PostMapping(value = "/action/user/profile")
	RestResponse<?> updateProfile(
		@Valid @RequestBody HelpdeskProfileCommandDto command, BindingResult validationResult
	);

}
