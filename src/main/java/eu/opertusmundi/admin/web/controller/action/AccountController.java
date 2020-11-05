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

import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.AccountFormDataDto;
import eu.opertusmundi.admin.web.model.dto.SetPasswordCommandDto;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;

public interface AccountController {

	@GetMapping(value = { "/action/admin/accounts" })
	RestResponse<PageResultDto<AccountDto>> find(
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "25") @Max(100) @Min(1) int size,
		@RequestParam(name = "name", defaultValue = "") String name,
		@RequestParam(name = "orderBy", defaultValue = "name") String orderBy,
		@RequestParam(name = "order", defaultValue = "asc") String order
	);

	@GetMapping(value = { "/action/admin/accounts/{id}" })
	RestResponse<AccountFormDataDto> findOne(@PathVariable int id);

	@PostMapping(value = "/action/admin/accounts")
	RestResponse<AccountDto> create(
		@Valid @RequestBody AccountCommandDto command, BindingResult validationResult
	);

	@PostMapping(value = { "/action/admin/accounts/{id}" })
	RestResponse<AccountDto> update(
		@PathVariable int id, @Valid @RequestBody AccountCommandDto command, BindingResult validationResult
	);

	@DeleteMapping(value = { "/action/admin/accounts/{id}" })
	RestResponse<Void> delete(@PathVariable int id);

	@PostMapping(value = { "/action/admin/accounts/{accountId}/role/{roleId}" })
	RestResponse<AccountDto> grantRole(@PathVariable int accountId, @PathVariable EnumRole roleId);

	@DeleteMapping(value = { "/action/admin/accounts/{accountId}/role/{roleId}" })
	RestResponse<AccountDto> revokeRole(@PathVariable int accountId, @PathVariable EnumRole roleId);

	@PostMapping(value = { "/action/user/password" })
	RestResponse<AccountDto> setUserPassword(@RequestBody SetPasswordCommandDto command, BindingResult validationResult);

}