package eu.opertusmundi.admin.web.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.opertusmundi.admin.web.model.AdminMessageCode;
import eu.opertusmundi.common.domain.HelpdeskAccountEntity;
import eu.opertusmundi.common.model.EnumAccountType;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.ServiceException;
import eu.opertusmundi.common.model.account.EnumAccountAttribute;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.common.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.common.model.account.helpdesk.RoleCommand;
import eu.opertusmundi.common.model.keycloak.server.UserDto;
import eu.opertusmundi.common.model.keycloak.server.UserQueryDto;
import eu.opertusmundi.common.repository.HelpdeskAccountRepository;
import eu.opertusmundi.common.service.KeycloakAdminService;

@Service
@Transactional
public class DefaultUserService implements UserService {

    private static final Logger logger = LogManager.getLogger(DefaultUserService.class);

    private static final int MIN_PASSWORD_LENGTH = 12;

    private static final CharacterRule[] PASSWORD_POLICY = new CharacterRule[] {
        new CharacterRule(EnglishCharacterData.Alphabetical),
        new CharacterRule(EnglishCharacterData.LowerCase),
        new CharacterRule(EnglishCharacterData.UpperCase),
        new CharacterRule(EnglishCharacterData.Digit),
    };

    private static final PasswordGenerator PASSWORD_GENERATOR = new PasswordGenerator();
    
    @Autowired
    private HelpdeskAccountRepository accountRepository;

    @Autowired(required = false)
    private KeycloakAdminService keycloakAdminService;
    
    @Override
    public PageResultDto<HelpdeskAccountDto> findAll(String email, Pageable pageable) {
        final PageResultDto<HelpdeskAccountDto> result = this.accountRepository.findAllByEmailContainsAsObjects(
            email,
            pageable
        );
        
        return result;
    }

    @Override
    public Optional<HelpdeskAccountDto> findOne(int id) {
        return this.accountRepository.findById(id).map(HelpdeskAccountEntity::toDto);
    }

    @Override
    public HelpdeskAccountDto create(Integer creatorId, HelpdeskAccountCommandDto command) throws ServiceException {
        if (keycloakAdminService != null) {
            final UUID idpUserId = this.idpCheckAccount(command.getEmail());

            if (idpUserId != null) {
                throw new ServiceException(
                    AdminMessageCode.IdpAccountAlreadyExists,
                    "An IDP account with the same user name already exists"
                );
            }
        }

        // Create local account
        final HelpdeskAccountDto account =  this.accountRepository.saveFrom(creatorId, command);

        // Create IDP account
        if (keycloakAdminService != null) {
            this.accountRepository.registerToIdp(account.getId());
            this.idpCreateAccount(account, command.getPassword());
        }

        return account;
    }

    @Override
    public HelpdeskAccountDto update(Integer creatorId, HelpdeskAccountCommandDto command) {
        return this.accountRepository.saveFrom(creatorId, command);
    }

    @Override
    public HelpdeskAccountDto grantRole(RoleCommand command) {
        HelpdeskAccountEntity       account   = this.accountRepository.findById(command.getAccountId()).orElse(null);
        final HelpdeskAccountEntity grantedBy = this.accountRepository.findById(command.getGrantedBy()).orElse(null);
        
        Assert.notNull(account, "Expected a non-null account");

        account.grant(command.getRole(), grantedBy);
        account = this.accountRepository.saveAndFlush(account);
        
        return account.toDto();
    }

    @Override
    public HelpdeskAccountDto revokeRole(RoleCommand command) {
        HelpdeskAccountEntity account = this.accountRepository.findById(command.getAccountId()).orElse(null);

        Assert.notNull(account, "Expected a non-null account");

        // TODO: Do not revoke the ADMIN role from the most recent administrator
        account.revoke(command.getRole());
        account = this.accountRepository.saveAndFlush(account);

        return account.toDto();
    }

    @Override
    public void setPassword(int id, String password) {
        this.resetPassword(id, password, false);
    }

    @Override
    public void delete(int id) {
        final HelpdeskAccountDto account = this.accountRepository.findById(id)
            .map(HelpdeskAccountEntity::toDto)
            .orElse(null);

        Assert.notNull(account, "Expected a non-null account");

        // Delete local account
        this.accountRepository.deleteById(id);

        // Delete account from IDP
        if (keycloakAdminService != null) {
            this.idpDeleteAccount(account);
        }
    }
    
    @Override
    public String registerToIdp(int id) {
        // Check local account
        final HelpdeskAccountDto account = this.accountRepository.findById(id)
            .map(HelpdeskAccountEntity::toDto)
            .orElse(null);
        
        Assert.notNull(account, "Expected a non-null account");

        if (keycloakAdminService == null) {
            // Ignore request if IDP is not configured
            return null;
        }

        // Update local account
        this.accountRepository.registerToIdp(id);
        
        // Ignore request if the user name is already registered to the IDP
        final UUID idpUserId = this.idpCheckAccount(account.getEmail());
        if (idpUserId != null) {
            return null;
        }
        
        // Register account with IDP
        final String otp = this.generatePassword();
        this.idpCreateAccount(account, otp);

        return otp;
    }
    
    @Override
    public String resetPassword(int id) {
        final String otp = this.generatePassword();

        return this.resetPassword(id, otp, true);
    }
    
    private String resetPassword(int id, String otp, boolean temporary) {
        // Check local account
        final HelpdeskAccountDto account = this.accountRepository.findById(id)
            .map(HelpdeskAccountEntity::toDto)
            .orElse(null);
        
        Assert.notNull(account, "Expected a non-null account");
        
        final String userName = account.getEmail();
        
        // Reset local password
        this.accountRepository.setPassword(id, otp);

        // Ignore request if IDP is not configured
        if (keycloakAdminService == null) {
            return otp;
        }

        // Ignore request if the user is not registered to the IDP
        final UUID idpUserId = this.idpCheckAccount(userName);
        if (idpUserId == null) {
            return otp;
        }
        
        // Reset IDP account password
        keycloakAdminService.resetPasswordForUser(idpUserId, otp, temporary /* temporary */);
        logger.info("The user [{}] is reset to have a temporary (OTP) password [id={}]", userName, idpUserId);

        return otp;
    }
    
    /**
     * Check if an account with the specified user name is already registered at
     * the IDP
     * 
     * @param userName
     * @return
     */
    private UUID idpCheckAccount(String userName) {
        Assert.hasText(userName, "Expected a non-empty user name");

        final UserQueryDto queryForUsername = new UserQueryDto();
        queryForUsername.setUsername(userName);
        queryForUsername.setExact(true);

        final List<UserDto> usersForUsername = keycloakAdminService.findUsers(queryForUsername);

        Assert.state(usersForUsername.size() < 2,
            () -> "Expected no more than one IDP user for a given username [username=" + userName + "]");
        
        return usersForUsername.stream().findFirst().map(UserDto::getId).orElse(null);
    }
    
    /**
     * Create a new account at the IDP from an existing local account
     * 
     * @param account
     * @param password
     */
    private void idpCreateAccount(HelpdeskAccountDto account, String password) throws ServiceException {
        Assert.notNull(account, "Expected a non-null account");
        Assert.hasText(password, "Expected a non-empty password");

        final String userName  = account.getEmail();
        final String userEmail = userName;
        final UUID   userKey   = account.getKey();
        
        Assert.hasText(userName, "Expected an non-empty username");
        logger.info("Setting up IDP account for user {} [key={}]", userName, userKey);
              
        try {
            // Create the user
            UserDto user = new UserDto();
            
            user = new UserDto();
            user.setUsername(userName);
            user.setEmail(userEmail);
            user.setEmailVerified(true);
            user.setEnabled(true);
            
            // Add opertusmundi-specific attributes (accountType etc.)
            user.setAttributes(
                Collections.singletonMap(EnumAccountAttribute.ACCOUNT_TYPE.key(), new String[]{EnumAccountType.OPERTUSMUNDI.name()})
            );
            
            UUID userId = keycloakAdminService.createUser(user);
            logger.info("Created helpdesk user [{}] on the IDP [id={}]", userName, userId);
            
            // Assert user is created
            user = keycloakAdminService.getUser(userId).get();
    
            Assert.state(user.getId() != null, "Expected a non-null user identifier (from the IDP side)");
            
            // Reset new user password
            keycloakAdminService.resetPasswordForUser(user.getId(), password, true /* temporary */);
            logger.info("The user [{}] is reset to have a temporary (OTP) password [id={}]", userName, user.getId());
        } catch (final Exception ex) {
            final String message = String.format(
                "Failed to create new IDP account. [userKey=%s, userName=%s]",
                userKey, userName
            );
            
            logger.error(message,  ex);

            throw new ServiceException(AdminMessageCode.IdpAccountCreateFailed, "Failed to create new IDP account");
        }
    }

    /**
     * Deletes an account from the IDP
     * 
     * @param account
     */
    private void idpDeleteAccount(HelpdeskAccountDto account) {
        Assert.notNull(account, "Expected a non-null account");

        final String userName = account.getEmail();
        final UUID   userKey  = account.getKey();

        Assert.hasText(userName, "Expected an non-empty username");
        logger.info("Deleting IDP account for user {} [key={}]", userName, userKey);
              
        try {
            // Find user
            final UserQueryDto queryForUsername = new UserQueryDto();
            queryForUsername.setUsername(userName);
            queryForUsername.setExact(true);

            final List<UserDto> usersForUsername = keycloakAdminService.findUsers(queryForUsername);

            Assert.state(usersForUsername.size() < 2,
                    () -> "Expected no more than one IDP user for a given username [username=" + userName + "]");
            
            if (usersForUsername.isEmpty()) {
                logger.warn("Failed to delete IDP account {}. Account was not found [key={}]", userName, userKey);
                return;
            }
            
            // Delete account
            final UserDto user = usersForUsername.get(0);

            keycloakAdminService.deleteUser(user.getId());
                       
            // Assert user is delete
            final UUID idpUserId = this.idpCheckAccount(userName);
    
            Assert.state(idpUserId == null, "Expected that the IDP account does not exist");
        } catch (final Exception ex) {
            final String message = String.format(
                "Failed to delete existing IDP account. [userKey=%s, userName=%s]",
                userKey, userName
            );
            
            logger.error(message,  ex);

            throw new ServiceException(AdminMessageCode.IdpAccountDeleteFailed, "Failed to delete existing IDP account");
        }
    }
    
    private String generatePassword() {
        return this.generatePassword(MIN_PASSWORD_LENGTH);
    }

    private String generatePassword(int length) {
        final String password = PASSWORD_GENERATOR.generatePassword(length, PASSWORD_POLICY);

        return password;
    }
    
}
