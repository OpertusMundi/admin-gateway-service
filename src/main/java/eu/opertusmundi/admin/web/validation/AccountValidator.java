package eu.opertusmundi.admin.web.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.model.BasicMessageCode;

@Component
public class AccountValidator implements Validator {

	@Autowired
	HelpdeskAccountRepository accountRepository;

	@Override
    public boolean supports(Class<?> clazz) {
		return AccountDto.class.isAssignableFrom(clazz);
	}

	@Override
    public void validate(Object obj, Errors e) {
		final AccountCommandDto a = (AccountCommandDto) obj;

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
			if (entity.hasRole(EnumRole.ADMIN)) {
				final Long admins = this.accountRepository.countUsersWithRole(EnumRole.ADMIN).orElse(0L);

				if (admins == 1) {
					// This is the last administrator
					if (!a.hasRole(EnumRole.ADMIN) || !a.isActive()) {
						e.rejectValue("roles", BasicMessageCode.CannotRevokeLastAdmin.key());
					}
				}
			}
		}
	}

}
