package eu.opertusmundi.admin.web.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.ProfileCommandDto;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "Account")
@Table(
    schema = "admin", name = "`account`",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_account_email", columnNames = {"`email`"}),
        @UniqueConstraint(name = "uq_account_key", columnNames = {"`key`"}),
        @UniqueConstraint(name = "uq_account_username", columnNames = {"`username`"}),
    }
)
public class AccountEntity {

    @Id
    @Column(name = "`id`", updatable = false)
    @SequenceGenerator(sequenceName = "`admin.account_id_seq`", name = "account_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "account_id_seq", strategy = GenerationType.SEQUENCE)
    @lombok.Getter
    Integer id ;

    @NotNull
    @NaturalId
    @Column(name = "key", updatable = false, columnDefinition = "uuid")
    @Getter
    private final UUID key = UUID.randomUUID();

    @NotNull
    @Column(name = "`username`", nullable = false, length = 120)
    @Getter
    @Setter
    String username;

    @Column(name = "`active`")
    @Getter
    @Setter
    boolean active = true;

    @Column(name = "`blocked`")
    @Getter
    @Setter
    boolean blocked = false;

    @NotNull
    @Email
    @Column(name = "`email`", nullable = false, length = 120)
    @Getter
    @Setter
    String email;

    @Column(name = "`email_verified`")
    @Getter
    @Setter
    boolean emailVerified = false;

    @Column(name = "`email_verified_on`")
    @Getter
    @Setter
    ZonedDateTime emailVerifiedOn;

    @Column(name = "`firstname`", length = 64)
    @Getter
    @Setter
    String firstName;

    @Column(name = "`lastname`", length = 64)
    @Getter
    @Setter
    String lastName;

    @NotNull
    @Pattern(regexp = "[a-z][a-z]")
    @Column(name = "`locale`")
    @Getter
    @Setter
    String locale;

    @Column(name = "`password`")
    @Getter
    @Setter
    String password;

    @Column(name = "`phone`", length = 15)
    @Getter
    @Setter
    private String phone;

    @Column(name = "`mobile`", length = 15)
    @Getter
    @Setter
    private String mobile;

    @Column(name = "image_binary")
    @Type(type = "org.hibernate.type.BinaryType")
    @Getter
    @Setter
    private byte[] image;

    @Column(name = "image_mime_type", length = 30)
    @Getter
    @Setter
    private String imageMimeType;

    @NotNull
    @Column(name = "`created_on`")
    @lombok.Getter
    @lombok.Setter
    ZonedDateTime createdOn;

    @NotNull
    @Column(name = "`modified_on`")
    @lombok.Getter
    @lombok.Setter
    ZonedDateTime modifiedOn;

    @OneToMany(
        mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true
    )
    List<AccountRoleEntity> roles = new ArrayList<>();

    public AccountEntity() {}

    public AccountEntity(int id) {
    	this.id = id;
    }

    public AccountEntity(String username) {
    	this.username = username;
    }

    public Set<EnumRole> getRoles() {
        final EnumSet<EnumRole> r = EnumSet.noneOf(EnumRole.class);
        for (final AccountRoleEntity ar: this.roles) {
            r.add(ar.role);
        }
        return r;
    }

    public boolean hasRole(EnumRole role) {
        for (final AccountRoleEntity ar: this.roles) {
            if (role == ar.role) {
                return true;
            }
        }
        return false;
    }

    public void grant(EnumRole role, AccountEntity grantedBy) {
        if (!this.hasRole(role)) {
            this.roles.add(new AccountRoleEntity(this, role, ZonedDateTime.now(), grantedBy));
        }
    }

    public void revoke(EnumRole role) {
        AccountRoleEntity target = null;
        for (final AccountRoleEntity ar: this.roles) {
            if (role == ar.role) {
                target = ar;
                break;
            }
        }
        if (target != null) {
            this.roles.remove(target);
        }
    }

    public void revokeAll() {
        this.roles.clear();
    }

    public void updateProfile(ProfileCommandDto command) {
        this.firstName     = command.getFirstName();
        this.image         = command.getImage();
        this.imageMimeType = command.getImageMimeType();
        this.lastName      = command.getLastName();
        this.locale        = command.getLocale();
        this.mobile        = command.getMobile();
        this.modifiedOn    = ZonedDateTime.now();
        this.phone         = command.getPhone();
    }

	public AccountDto toDto() {
		final AccountDto a = new AccountDto();

		a.setActive(this.active);
		a.setBlocked(this.blocked);
        a.setCreatedOn(this.createdOn);
		a.setEmail(this.email);
		a.setEmailVerified(this.emailVerified);
		a.setEmailVerifiedOn(this.emailVerifiedOn);
		a.setFirstName(this.firstName);
		a.setId(this.id);
		a.setImage(this.image);
		a.setImageMimeType(this.imageMimeType);
		a.setKey(this.key);
		a.setLastName(this.lastName);
		a.setLocale(this.locale);
		a.setMobile(this.mobile);
		a.setModifiedOn(this.modifiedOn);
		a.setPhone(this.phone);
		a.setRoles(this.getRoles());
		a.setUsername(this.username);

		return a;
	}

}
