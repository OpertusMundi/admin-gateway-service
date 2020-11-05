package eu.opertusmundi.admin.web.repository;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.domain.AccountEntity;
import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.ProfileCommandDto;

@Repository
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

	@Query("SELECT count(a) FROM Account a INNER JOIN a.roles r WHERE r.role = :role")
	long countByRole(@Param("role") EnumRole role);

	Optional<AccountEntity> findOneByUsername(String username);

	Optional<AccountEntity> findOneByUsernameAndIdNot(String username, Integer id);

	Optional<AccountEntity> findOneByEmail(String email);

	Optional<AccountEntity> findOneByEmailAndIdNot(String email, Integer id);

	@Query("select count(a) from Account a inner join a.roles r where r.role = :role")
	Optional<Long> countUsersWithRole(@Param("role") EnumRole role);

	@Query("SELECT a FROM Account a WHERE a.username like :username")
	Page<AccountEntity> findAllByUsernameContains(
		@Param("username")String username,
		Pageable pageable
	);

	@Modifying
	@Query("UPDATE Account a SET a.active = :active WHERE a.id = :id")
	@Transactional(readOnly = false)
	void setActive(@Param("id") Integer id, @Param("active") boolean active);

	@Modifying
	@Query("UPDATE Account a SET a.blocked = :blocked WHERE a.id = :id")
	@Transactional(readOnly = false)
	void setBlocked(@Param("id") Integer id, @Param("blocked") boolean blocked);

	@Transactional(readOnly = false)
	default AccountDto setPassword(Integer id, String password) {

		// Retrieve entity from repository
		final AccountEntity accountEntity = this.findById(id).orElse(null);

		if (accountEntity == null) {
			throw new EntityNotFoundException();
		}

		final PasswordEncoder encoder = new BCryptPasswordEncoder();

		accountEntity.setPassword(encoder.encode(password));
		accountEntity.setModifiedOn(ZonedDateTime.now());

		return this.saveAndFlush(accountEntity).toDto();
	}

	@Transactional(readOnly = false)
	default AccountDto saveFrom(Integer creatorId, AccountCommandDto command) {
        final ZonedDateTime now     = ZonedDateTime.now();
        final AccountEntity creator = creatorId == null ? null : this.findById(creatorId).orElse(null);

		AccountEntity accountEntity = null;

		if (command.getId() != null) {
			// Retrieve entity from repository
			accountEntity = this.findById(command.getId()).orElse(null);

			if (accountEntity == null) {
				throw new EntityNotFoundException();
			}
		} else {
			// Create a new entity
			accountEntity = new AccountEntity();

            accountEntity.setCreatedOn(now);
            accountEntity.setEmail(command.getEmail());
            accountEntity.setEmailVerified(false);
            accountEntity.setUsername(command.getUsername());

            final PasswordEncoder encoder = new BCryptPasswordEncoder();
            accountEntity.setPassword(encoder.encode(command.getPassword()));
		}

		// Account properties
		accountEntity.setActive(command.isActive());
		accountEntity.setBlocked(command.isBlocked());
		accountEntity.setFirstName(command.getFirstName());
		accountEntity.setImage(command.getImage());
		accountEntity.setImageMimeType(command.getImageMimeType());
		accountEntity.setLastName(command.getLastName());
		accountEntity.setLocale(command.getLocale());
		accountEntity.setMobile(command.getMobile());
		accountEntity.setModifiedOn(now);
		accountEntity.setPhone(command.getPhone());

		// Roles
		final EnumRole[] currentRoles = accountEntity.getRoles().stream().toArray(EnumRole[]::new);
		for (final EnumRole role : currentRoles) {
			if (!command.getRoles().contains(role)) {
				accountEntity.revoke(role);
			}
		}

		for (final EnumRole role : command.getRoles()) {
			if (!accountEntity.hasRole(role)) {
				accountEntity.grant(role, creator);
			}
		}

		return this.saveAndFlush(accountEntity).toDto();
	}

	@Transactional(readOnly = false)
	default AccountDto saveProfile(ProfileCommandDto command) {
		AccountEntity accountEntity = null;

		if (command.getId() != null) {
			// Retrieve entity from repository
			accountEntity = this.findById(command.getId()).orElse(null);

			if (accountEntity == null) {
				throw new EntityNotFoundException();
			}
		} else {
			throw new EntityNotFoundException();
		}

		accountEntity.updateProfile(command);

		return this.saveAndFlush(accountEntity).toDto();
	}

}
