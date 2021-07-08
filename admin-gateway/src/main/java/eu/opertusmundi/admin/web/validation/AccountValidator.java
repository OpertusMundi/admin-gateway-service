package eu.opertusmundi.admin.web.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eu.opertusmundi.common.domain.HelpdeskAccountEntity;
import eu.opertusmundi.common.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.model.BasicMessageCode;
import eu.opertusmundi.common.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;

@Component
public class AccountValidator implements Validator {

	@Autowired
	HelpdeskAccountRepository accountRepository;

	@Override
    public boolean supports(Class<?> clazz) {
		return HelpdeskAccountDto.class.isAssignableFrom(clazz);
	}

	@Override
    public void validate(Object obj, Errors e) {
		final HelpdeskAccountCommandDto a = (HelpdeskAccountCommandDto) obj;

		// Email must be unique
        HelpdeskAccountEntity entity = a.getId() == null ? this.accountRepository.findOneByEmail(a.getEmail()).orElse(null) : this.accountRepository.findOneByEmailAndIdNot(a.getEmail(), a.getId()).orElse(null);

		if (entity != null) {
			e.rejectValue(
		        "email", BasicMessageCode.ValidationNotUnique.key(), new Object[] {a.getEmail()}, "Email must be unique"
	        );
		}

		// Password
		if (!StringUtils.isBlank(a.getPassword()) &&
			!StringUtils.isBlank(a.getPasswordMatch()) &&
			!a.getPassword().equals(a.getPasswordMatch())) {
			e.rejectValue("passwordMatch", BasicMessageCode.ValidationValueMismatch.key());
		}

		if (a.getId() == null && StringUtils.isBlank(a.getPassword())) {
			e.rejectValue("password", BasicMessageCode.ValidationRequired.key());
		}

		// Check roles
		if (a.getId() != null) {
			entity = this.accountRepository.findById(a.getId()).get();

			// Check if this is the last administrator
			if (entity.hasRole(EnumHelpdeskRole.ADMIN)) {
				final Long admins = this.accountRepository.countUsersWithRole(EnumHelpdeskRole.ADMIN).orElse(0L);

				if (admins == 1) {
					// This is the last administrator
					if (!a.hasRole(EnumHelpdeskRole.ADMIN) || !a.isActive()) {
						e.rejectValue("roles", BasicMessageCode.CannotRevokeLastAdmin.key());
					}
				}
			}
		}
	}

}
