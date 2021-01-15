package eu.opertusmundi.admin.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.opertusmundi.admin.web.model.dto.SetPasswordCommandDto;
import eu.opertusmundi.common.model.BasicMessageCode;

@Component
public class PasswordValidator implements Validator {

	@Override
    public boolean supports(Class<?> clazz) {
		return SetPasswordCommandDto.class.isAssignableFrom(clazz);
	}

	@Override
    public void validate(Object obj, Errors e) {
		final SetPasswordCommandDto c = (SetPasswordCommandDto) obj;

		ValidationUtils.rejectIfEmptyOrWhitespace(e, "password", BasicMessageCode.ValidationRequired.key());
		ValidationUtils.rejectIfEmptyOrWhitespace(e, "passwordMatch", BasicMessageCode.ValidationRequired.key());

		if (!e.hasErrors() && !c.getPassword().equals(c.getPasswordMatch())) {
			e.rejectValue("passwordMatch", BasicMessageCode.ValidationValueMismatch.key());
		}
	}

}
