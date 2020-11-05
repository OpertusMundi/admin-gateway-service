package eu.opertusmundi.admin.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumRole
{
    ADMIN       ("system administrator"),
    USER       	("User"),
    DEVELOPER  	("Developer role that enables additional experimental features"),
    ;

	private final String description;

}
