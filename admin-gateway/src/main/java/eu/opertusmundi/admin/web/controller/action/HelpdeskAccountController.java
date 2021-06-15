package eu.opertusmundi.admin.web.controller.action;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskAccountSortField;
import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountFormDataDto;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskSetPasswordCommandDto;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;

public interface HelpdeskAccountController {

	@GetMapping(value = { "/action/helpdesk/accounts" })
	RestResponse<PageResultDto<HelpdeskAccountDto>> find(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
		@RequestParam(name = "name", defaultValue = "") String name,
		@RequestParam(name = "orderBy", defaultValue = "EMAIL") EnumHelpdeskAccountSortField orderBy,
		@RequestParam(name = "order", defaultValue = "ASC") EnumSortingOrder order
	);

	@GetMapping(value = { "/action/helpdesk/accounts/{id}" })
	RestResponse<HelpdeskAccountFormDataDto> findOne(@PathVariable int id);

	@PostMapping(value = "/action/helpdesk/accounts")
	RestResponse<HelpdeskAccountDto> create(
		@Valid @RequestBody HelpdeskAccountCommandDto command, BindingResult validationResult
	);

	@PostMapping(value = { "/action/helpdesk/accounts/{id}" })
	RestResponse<HelpdeskAccountDto> update(
		@PathVariable int id, @Valid @RequestBody HelpdeskAccountCommandDto command, BindingResult validationResult
	);

	@DeleteMapping(value = { "/action/helpdesk/accounts/{id}" })
	RestResponse<Void> delete(@PathVariable int id);

	@PostMapping(value = { "/action/helpdesk/accounts/{accountId}/role/{roleId}" })
	RestResponse<HelpdeskAccountDto> grantRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId);

	@DeleteMapping(value = { "/action/helpdesk/accounts/{accountId}/role/{roleId}" })
	RestResponse<HelpdeskAccountDto> revokeRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId);

	@PostMapping(value = { "/action/user/password" })
	RestResponse<HelpdeskAccountDto> setUserPassword(@RequestBody HelpdeskSetPasswordCommandDto command, BindingResult validationResult);

}