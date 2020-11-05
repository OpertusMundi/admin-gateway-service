package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import eu.opertusmundi.admin.web.model.EnumRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class AccountBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean active;

    private boolean blocked;

    @NotEmpty
    private String email;

    @NotEmpty
    private String firstName;

    private byte[] image;

    private String imageMimeType;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String locale;

    @NotEmpty
    private String mobile;

    private String phone;

    private Set<EnumRole> roles;

    @NotEmpty
    private String username;

    public boolean hasRole(EnumRole role) {
        return this.roles.contains(role);
    }

}
