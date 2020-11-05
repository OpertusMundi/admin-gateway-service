package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccountFormDataDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private AccountDto account;

}
