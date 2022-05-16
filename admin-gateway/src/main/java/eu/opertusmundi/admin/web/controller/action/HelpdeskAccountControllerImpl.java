package eu.opertusmundi.admin.web.controller.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.admin.web.model.AdminMessageCode;
import eu.opertusmundi.admin.web.service.UserService;
import eu.opertusmundi.admin.web.validation.AccountValidator;
import eu.opertusmundi.admin.web.validation.PasswordValidator;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskAccountSortField;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountFormDataDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskSetPasswordCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.RoleCommand;

@RestController
@Secured({ "ROLE_ADMIN" })
public class HelpdeskAccountControllerImpl extends BaseController implements HelpdeskAccountController {

	@Autowired
    private AccountValidator accountValidator;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private UserService userService;

	@Override
	public RestResponse<PageResultDto<HelpdeskAccountDto>> find(
		int page, int size, String name, EnumHelpdeskAccountSortField orderBy, EnumSortingOrder order
	) {
        final Direction   direction   = order == EnumSortingOrder.DESC ? Direction.DESC : Direction.ASC;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy.getValue()));
        final String      param       = "%" + name + "%";

        final PageResultDto<HelpdeskAccountDto> result = this.userService.findAll(param, pageRequest);

        return RestResponse.result(result);
	}

    @Override
    public RestResponse<HelpdeskAccountFormDataDto> findOne(int id) {
        final HelpdeskAccountDto account = this.userService.findOne(id).orElse(null);

        if (account == null) {
            return RestResponse.notFound();
        }

        final HelpdeskAccountFormDataDto result = new HelpdeskAccountFormDataDto();

        result.setAccount(account);

        return RestResponse.result(result);
    }

	@Override
    public RestResponse<HelpdeskAccountDto> create(HelpdeskAccountCommandDto command, BindingResult validationResult) {
        try {
            this.accountValidator.validate(command, validationResult);

            if (validationResult.hasErrors()) {
                return RestResponse.invalid(validationResult.getFieldErrors());
            }

            final HelpdeskAccountDto result = this.userService.create(this.currentUserId(), command);
            return RestResponse.result(result);

        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
	}

	@Override
	public RestResponse<HelpdeskAccountDto> update(int id, HelpdeskAccountCommandDto command, BindingResult validationResult) {
	    command.setId(id);

		this.accountValidator.validate(command, validationResult);

		if (validationResult.hasErrors()) {
			return RestResponse.invalid(validationResult.getFieldErrors());
		}

		final HelpdeskAccountDto result = this.userService.update(this.currentUserId(), command);

		return RestResponse.result(result);
	}

	@Override
	public RestResponse<Void> delete(int id) {
        final HelpdeskAccountDto account = this.userService.findOne(id).orElse(null);
        if (account == null) {
            return RestResponse.notFound();
        }

		if (this.currentUserId().equals(id)) {
			return RestResponse.failure(AdminMessageCode.CannotDeleteSelf, "Cannot delete the current authenticated account");
		}
		
		this.userService.delete(id);

		return RestResponse.success();
	}

    @Override
    public RestResponse<String> registerToIdp(int id) {
        try {
            HelpdeskAccountDto account = this.userService.findOne(id).orElse(null);
            if (account == null) {
                return RestResponse.notFound();
            }

            final String otp = this.userService.registerToIdp(id);
            return RestResponse.result(otp);

        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
    }
    
    @Override
    public RestResponse<String> resetPassword(@PathVariable int id) {
        try {
            HelpdeskAccountDto account = this.userService.findOne(id).orElse(null);
            if (account == null) {
                return RestResponse.notFound();
            }

            final String otp = this.userService.resetPassword(id);
            return RestResponse.result(otp);

        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
    }

	@Override
	public RestResponse<HelpdeskAccountDto> grantRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId) {
	    try {
    	    final HelpdeskAccountDto account = this.userService.findOne(accountId).orElse(null);
    		if (account == null) {
    			return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
    		}
    		
            final RoleCommand        command = RoleCommand.of(accountId, this.currentUserId(), roleId);
            final HelpdeskAccountDto result  = this.userService.grantRole(command);
    
            return RestResponse.result(result);
        } catch (ServiceException ex) {
            return RestResponse.failure(ex);
        }
	}

	@Override
	public RestResponse<HelpdeskAccountDto> revokeRole(@PathVariable int accountId, @PathVariable EnumHelpdeskRole roleId) {
	    final HelpdeskAccountDto account = this.userService.findOne(accountId).orElse(null);
		if (account == null) {
			return RestResponse.failure(BasicMessageCode.RecordNotFound, "Account was not found");
		}

        final RoleCommand        command = RoleCommand.of(accountId, this.currentUserId(), roleId);
        final HelpdeskAccountDto result  = this.userService.revokeRole(command);

        return RestResponse.result(result);
    }

	@Override
	@Secured({ "ROLE_USER" })
	public RestResponse<Void> setUserPassword(HelpdeskSetPasswordCommandDto command, BindingResult validationResult) {
		this.passwordValidator.validate(command, validationResult);
        if (validationResult.hasErrors()) {
            return RestResponse.invalid(validationResult.getFieldErrors());
        }

        this.userService.setPassword(this.currentUserId(), command.getPassword());

        return RestResponse.success();
    }

}
