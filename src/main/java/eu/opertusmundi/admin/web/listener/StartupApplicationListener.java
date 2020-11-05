package eu.opertusmundi.admin.web.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.model.dto.AccountCommandDto;
import eu.opertusmundi.admin.web.repository.AccountRepository;

@Profile({"development", "production"})
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(StartupApplicationListener.class);

	@Value("${opertus-mundi.default-admin.username:admin}")
	private String username;

	@Value("${opertus-mundi.default-admin.password:}")
    private String password;

	@Value("${opertus-mundi.default-admin.firstname:Default Admin}")
    private String firstname;

	@Value("${opertus-mundi.default-admin.lastname:}")
    private String lastname;

	@Autowired
	AccountRepository accountRepository;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// Check if at least one organization exists
		try {
			final long count = this.accountRepository.count();

			if (count == 0) {
				this.initializeDefaultAccount();
			}
		} catch (final Exception ex) {
			logger.error("Failed to initialize application default account", ex);
		}
	}

	@Transactional
	private void initializeDefaultAccount() {
		// Create default organization and system administrator account
		try {
			// Create default user
			final AccountCommandDto command = new AccountCommandDto();
            final boolean           logPassword = StringUtils.isBlank(this.password);
            final String            password    = logPassword ? UUID.randomUUID().toString() : this.password;

			command.setActive(true);
			command.setBlocked(false);
			command.setEmail(this.username);
			command.setFirstName(this.firstname);
			command.setLastName(this.lastname);
			command.setLocale("en");
			command.setUsername(this.username);
			command.setPassword(password);

			final EnumRole[] roleArray = { EnumRole.USER, EnumRole.ADMIN };
			final Set<EnumRole> roleSet = new HashSet<EnumRole>(Arrays.asList(roleArray));

			command.setRoles(roleSet);

			this.accountRepository.saveFrom(null, command);

			if(logPassword) {
    			logger.info(
    				"Default admin user [{}] have been created. Password is [{}].",
    				this.username, password
    			);
			} else {
                logger.info("Default admin user [{}] have been created.", this.username);
			}
		} catch (final Exception ex) {
			logger.error("Failed to initialize application default account", ex);
		}
	}

}
