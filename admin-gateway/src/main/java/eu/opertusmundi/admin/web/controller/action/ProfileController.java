package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.opertusmundi.admin.web.model.dto.ProfileCommandDto;
import eu.opertusmundi.common.model.RestResponse;

public interface ProfileController {

	@RequestMapping(value = "/action/user/profile", method = RequestMethod.GET)
	RestResponse<?> getProfile(Authentication authentication);

	@RequestMapping(value = "/action/user/profile", method = RequestMethod.POST)
	RestResponse<?> updateProfile(
		@Valid @RequestBody ProfileCommandDto command, BindingResult validationResult
	);

}
