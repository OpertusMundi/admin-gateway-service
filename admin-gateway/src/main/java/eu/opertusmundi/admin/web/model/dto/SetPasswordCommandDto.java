package eu.opertusmundi.admin.web.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SetPasswordCommandDto {

	private String password;

	private String passwordMatch;

}
