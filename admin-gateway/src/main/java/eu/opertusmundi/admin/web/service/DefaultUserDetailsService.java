package eu.opertusmundi.admin.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.opertusmundi.admin.web.domain.HelpdeskAccountEntity;
import eu.opertusmundi.admin.web.model.account.helpdesk.EnumHelpdeskRole;
import eu.opertusmundi.admin.web.model.account.helpdesk.HelpdeskAccountDto;
import eu.opertusmundi.admin.web.repository.HelpdeskAccountRepository;

@Service
public class DefaultUserDetailsService implements UserDetailsService
{
    public static class Details implements UserDetails
    {
        private static final long serialVersionUID = 1L;

        private final HelpdeskAccountDto account;

        private final String password;

        public Details(HelpdeskAccountDto account, String password)
        {
            this.account = account;
            this.password = password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities()
        {
            final List<GrantedAuthority> authorities = new ArrayList<>();
            for (final EnumHelpdeskRole role: this.account.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            }
            return authorities;
        }

        public Integer getId() {
            return this.account.getId();
        }

        public UUID getKey() {
            return this.account.getKey();
        }

        @Override
        public String getPassword()
        {
            return this.password;
        }

        @Override
        public String getUsername()
        {
            return this.account.getEmail();
        }

        @Override
        public boolean isAccountNonExpired()
        {
            return this.account.isActive();
        }

        @Override
        public boolean isAccountNonLocked()
        {
            return !this.account.isBlocked();
        }

        @Override
        public boolean isCredentialsNonExpired()
        {
            return this.account.isActive();
        }

        @Override
        public boolean isEnabled()
        {
            return this.account.isActive();
        }

        public String getLocale() {
            return this.account.getLocale();
        }

        public boolean hasRole(EnumHelpdeskRole role) {
            return this.account.hasRole(role);
        }
    }

    @Autowired
    private HelpdeskAccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
		final HelpdeskAccountEntity accountEntity = this.accountRepository.findOneByEmail(username).orElse(null);
		if (accountEntity == null) {
			throw new UsernameNotFoundException(username);
		}
		if (!accountEntity.isActive()) {
			throw new UsernameNotFoundException(username);
		}

        return new Details(accountEntity.toDto(), accountEntity.getPassword());
    }
}
