package eu.opertusmundi.admin.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.opertusmundi.common.model.account.helpdesk.HelpdeskSetPasswordCommandDto;
import eu.opertusmundi.common.model.BasicMessageCode;

@Component
public class PasswordValidator implements Validator {

	@Override
    public boolean supports(Class<?> clazz) {
		return HelpdeskSetPasswordCommandDto.class.isAssignableFrom(clazz);
	}

	@Override
    public void validate(Object obj, Errors e) {
		final HelpdeskSetPasswordCommandDto c = (HelpdeskSetPasswordCommandDto) obj;

		ValidationUtils.rejectIfEmptyOrWhitespace(e, "password", BasicMessageCode.ValidationRequired.key());
		ValidationUtils.rejectIfEmptyOrWhitespace(e, "passwordMatch", BasicMessageCode.ValidationRequired.key());

		if (!e.hasErrors() && !c.getPassword().equals(c.getPasswordMatch())) {
			e.rejectValue("passwordMatch", BasicMessageCode.ValidationValueMismatch.key());
		}
	}

}
