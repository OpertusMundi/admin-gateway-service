package eu.opertusmundi.admin.web.controller.action;

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

import eu.opertusmundi.admin.web.validation.AccountValidator;
import eu.opertusmundi.admin.web.validation.PasswordValidator;
import eu.opertusmundi.common.domain.HelpdeskAccountEntity;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskAccountSortField;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountFormDataDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskSetPasswordCommandDto;
import eu.opertusmundi.common.repository.HelpdeskAccountRepository;

@RestController
@Secured({ "ROLE_ADMIN" })
public class HelpdeskAccountControllerImpl extends BaseController implements HelpdeskAccountController {

	@Autowired
	private HelpdeskAccountRepository accountRepository;

	@Autowired
	private AccountValidator accountValidator;

	@Autowired
	private PasswordValidator passwordValidator;

	@Override
	public RestResponse<PageResultDto<HelpdeskAccountDto>> find(
		int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
	) {

        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));

		final String param = "%" + name + "%";

		final Page<HelpdeskAccountEntity> entities = this.accountRepository.findAllByEmailContains(
			param,
			pageRequest
		);

		final Page<HelpdeskAccountDto> p = entities.map(HelpdeskAccountEntity::toDto);

		final long count = p.getTotalElements();
		final List<HelpdeskAccountDto> records = p.stream().collect(Collectors.toList());
		final PageResultDto<HelpdeskAccountDto> result = PageResultDto.of(page, size, records, count);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<HelpdeskAccountFormDataDto> findOne(int id) {
		final HelpdeskAccountEntity e = this.accountRepository.findById(id).orElse(null);

		if (e == null) {
			return RestResponse.notFound();
		}

		final HelpdeskAccountFormDataDto result = new HelpdeskAccountFormDataDto();

		result.setAccount(e.toDto());

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<HelpdeskAccountDto> create(HelpdeskAccountCommandDto command, BindingResult validationResult) {
        this.accountValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final HelpdeskAccountDto result = this.accountRepository.saveFrom(this.currentUserId(), command);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<HelpdeskAccountDto> update(int id, HelpdeskAccountCommandDto command, BindingResult validationResult) {
	    command.setId(id);

		this.accountValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final HelpdeskAccountDto result = this.accountRepository.saveFrom(this.currentUserId(), command);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<Void> delete(int id) {
		final HelpdeskAccountEntity e = this.accountRepository.findById(id).orElse(null);

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
	public RestResponse<HelpdeskAccountDto> grantRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId) {

		final HelpdeskAccountEntity account = this.accountRepository.findById(accountId).orElse(null);
		if (account == null) {
			return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
		}

		final HelpdeskAccountEntity grantedBy = this.accountRepository.findById(this.currentUserId()).orElse(null);

		account.grant(roleId, grantedBy);

		this.accountRepository.save(account);

		return RestResponse.result(account.toDto());
	}

	@Override
	public RestResponse<HelpdeskAccountDto> revokeRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId) {

		final HelpdeskAccountEntity account = this.accountRepository.findById(accountId).orElse(null);
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
	public RestResponse<HelpdeskAccountDto> setUserPassword(HelpdeskSetPasswordCommandDto command, BindingResult validationResult) {
		this.passwordValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final HelpdeskAccountDto account = this.accountRepository.setPassword(this.currentUserId(), command.getPassword());

		return RestResponse.result(account);
	}

}
