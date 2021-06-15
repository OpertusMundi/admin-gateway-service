package eu.opertusmundi.admin.web.model.account.helpdesk;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HelpdeskSetPasswordCommandDto {

	private String password;

	private String passwordMatch;

}
