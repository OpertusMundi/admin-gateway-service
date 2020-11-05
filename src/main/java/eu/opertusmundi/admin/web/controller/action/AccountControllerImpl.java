package eu.opertusmundi.admin.web.controller.action;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.domain.AccountEntity;
import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.AccountFormDataDto;
import eu.opertusmundi.admin.web.model.dto.SetPasswordCommandDto;
import eu.opertusmundi.admin.web.repository.AccountRepository;
import eu.opertusmundi.admin.web.validation.AccountValidator;
import eu.opertusmundi.admin.web.validation.PasswordValidator;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_USER" })
public class AccountControllerImpl extends BaseController implements AccountController {

	private final List<String> sortableFields = Arrays.asList("username", "customer.name");

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountValidator accountValidator;

	@Autowired
	private PasswordValidator passwordValidator;

	@Override
	public RestResponse<PageResultDto<AccountDto>> find(
		int page, int size, String name, String orderBy, String order
	) {

		final Direction direction = order.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
		if (!this.sortableFields.contains(orderBy)) {
			orderBy = "username";
		}

		final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy));

		final String param = "%" + name + "%";

		final Page<AccountEntity> entities = this.accountRepository.findAllByUsernameContains(
			param,
			pageRequest
		);

		final Page<AccountDto> p = entities.map(AccountEntity::toDto);

		final long count = p.getTotalElements();
		final List<AccountDto> records = p.stream().collect(Collectors.toList());
		final PageResultDto<AccountDto> result = PageResultDto.of(page, size, records, count);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<AccountFormDataDto> findOne(int id) {
		final AccountEntity e = this.accountRepository.findById(id).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}

		final AccountFormDataDto result = new AccountFormDataDto();

		result.setAccount(e.toDto());

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<AccountDto> create(AccountCommandDto command, BindingResult validationResult) {
        this.accountValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final AccountDto result = this.accountRepository.saveFrom(this.currentUserId(), command);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<AccountDto> update(int id, AccountCommandDto command, BindingResult validationResult) {
	    command.setId(id);

		this.accountValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final AccountDto result = this.accountRepository.saveFrom(this.currentUserId(), command);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<Void> delete(int id) {
		final AccountEntity e = this.accountRepository.findById(id).orElse(null);

		if(e == null) {
			return RestResponse.notFound();
		}

		if (this.currentUserId().equals(id)) {
			return RestResponse.failure(BasicMessageCode.CannotDeleteSelf, "Cannot delete the current authenticated account");
		}
		this.accountRepository.deleteById(id);

		return RestResponse.success();
	}

	@Override
	public RestResponse<AccountDto> grantRole(@PathVariable int accountId, @PathVariable EnumRole roleId) {

		final AccountEntity account = this.accountRepository.findById(accountId).orElse(null);
		if (account == null) {
			return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
		}

		final AccountEntity grantedBy = this.accountRepository.findById(this.currentUserId()).orElse(null);

		account.grant(roleId, grantedBy);

		this.accountRepository.save(account);

		return RestResponse.result(account.toDto());
	}

	@Override
	public RestResponse<AccountDto> revokeRole(@PathVariable int accountId, @PathVariable EnumRole roleId) {

		final AccountEntity account = this.accountRepository.findById(accountId).orElse(null);
		if (account == null) {
			return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
		}

		// TODO: Do not revoke the ADMIN role from the most recent administrator
		account.revoke(roleId);

		this.accountRepository.save(account);

		return RestResponse.result(account.toDto());
	}

	@Override
	@Secured({ "ROLE_USER" })
	public RestResponse<AccountDto> setUserPassword(SetPasswordCommandDto command, BindingResult validationResult) {
		this.passwordValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final AccountDto account = this.accountRepository.setPassword(this.currentUserId(), command.getPassword());

		return RestResponse.result(account);
	}

}
