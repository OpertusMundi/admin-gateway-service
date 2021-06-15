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

import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountCommandDto;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;

@Profile({"development", "production"})
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(StartupApplicationListener.class);

	@Value("${opertus-mundi.default-admin.username:admin}")
	private String username;

	@Value("${opertus-mundi.default-admin.password:}")
    private String password;

	@Value("${opertus-mundi.default-admin.firstName:Default Admin}")
    private String firstName;

	@Value("${opertus-mundi.default-admin.lastName:}")
    private String lastName;

	@Autowired
	HelpdeskAccountRepository accountRepository;

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
			final HelpdeskAccountCommandDto command = new HelpdeskAccountCommandDto();
            final boolean           logPassword = StringUtils.isBlank(this.password);
            final String            password    = logPassword ? UUID.randomUUID().toString() : this.password;

			command.setActive(true);
			command.setBlocked(false);
			command.setEmail(this.username);
			command.setFirstName(this.firstName);
			command.setLastName(this.lastName);
			command.setLocale("en");
			command.setPassword(password);

			final EnumHelpdeskRole[] roleArray = { EnumHelpdeskRole.USER, EnumHelpdeskRole.ADMIN };
			final Set<EnumHelpdeskRole> roleSet = new HashSet<EnumHelpdeskRole>(Arrays.asList(roleArray));

			command.setRoles(roleSet);

			this.accountRepository.saveFrom(null, command);

			if(logPassword) {
    			logger.info(
    				"Default admin user have been created. [username={}, password={}]",
    				this.username, password
    			);
			} else {
                logger.info("Default admin user have been created. [username={}]", this.username);
			}
		} catch (final Exception ex) {
			logger.error("Failed to initialize application default account", ex);
		}
	}

}
