package eu.opertusmundi.admin.web.model.account.helpdesk;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HelpdeskAccountFormDataDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private HelpdeskAccountDto account;

}
