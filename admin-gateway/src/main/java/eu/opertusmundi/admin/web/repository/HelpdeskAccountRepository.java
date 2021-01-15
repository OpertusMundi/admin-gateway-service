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

import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.model.dto.AccountDto;
import eu.opertusmundi.admin.web.model.dto.ProfileCommandDto;

@Repository
@Transactional(readOnly = true)
public interface HelpdeskAccountRepository extends JpaRepository<HelpdeskAccountEntity, Integer> {

	@Query("SELECT count(a) FROM HelpdeskAccount a INNER JOIN a.roles r WHERE r.role = :role")
	long countByRole(@Param("role") EnumRole role);

	Optional<HelpdeskAccountEntity> findOneByEmail(String email);

	Optional<HelpdeskAccountEntity> findOneByEmailAndIdNot(String email, Integer id);

	@Query("select count(a) from HelpdeskAccount a inner join a.roles r where r.role = :role")
	Optional<Long> countUsersWithRole(@Param("role") EnumRole role);

	@Query("SELECT a FROM HelpdeskAccount a WHERE a.email like :email")
	Page<HelpdeskAccountEntity> findAllByEmailContains(
		@Param("email")String email,
		Pageable pageable
	);

	@Modifying
	@Query("UPDATE HelpdeskAccount a SET a.active = :active WHERE a.id = :id")
	@Transactional(readOnly = false)
	void setActive(@Param("id") Integer id, @Param("active") boolean active);

	@Modifying
	@Query("UPDATE HelpdeskAccount a SET a.blocked = :blocked WHERE a.id = :id")
	@Transactional(readOnly = false)
	void setBlocked(@Param("id") Integer id, @Param("blocked") boolean blocked);

	@Transactional(readOnly = false)
	default AccountDto setPassword(Integer id, String password) {

		// Retrieve entity from repository
		final HelpdeskAccountEntity accountEntity = this.findById(id).orElse(null);

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
        final HelpdeskAccountEntity creator = creatorId == null ? null : this.findById(creatorId).orElse(null);

		HelpdeskAccountEntity accountEntity = null;

		if (command.getId() != null) {
			// Retrieve entity from repository
			accountEntity = this.findById(command.getId()).orElse(null);

			if (accountEntity == null) {
				throw new EntityNotFoundException();
			}
		} else {
			// Create a new entity
			accountEntity = new HelpdeskAccountEntity();

            accountEntity.setCreatedOn(now);
            accountEntity.setEmail(command.getEmail());
            accountEntity.setEmailVerified(false);

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
		HelpdeskAccountEntity accountEntity = null;

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
