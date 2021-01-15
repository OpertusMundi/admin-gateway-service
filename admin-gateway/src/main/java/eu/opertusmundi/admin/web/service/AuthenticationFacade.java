package eu.opertusmundi.admin.web.service;

import java.util.Locale;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.opertusmundi.admin.web.model.EnumRole;
import eu.opertusmundi.admin.web.service.DefaultUserDetailsService.Details;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication;
    }

    @Override
    public boolean isAuthenticated() {
        final Authentication authentication = this.getAuthentication();

        return authentication != null && authentication.isAuthenticated();
    }

    @Override
    public boolean isSystemAdmin() {
        return this.hasRole(EnumRole.ADMIN);
    }

    @Override
    public boolean hasRole(EnumRole role) {
        final Authentication authentication = this.getAuthentication();
        if (authentication == null) {
            return false;
        }
        return ((Details) authentication.getPrincipal()).hasRole(role);
    }

    @Override
    public boolean hasAnyRole(EnumRole... roles) {
        if (roles == null) {
            return false;
        }
        for (final EnumRole role : roles) {
            if (this.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getCurrentUserId() {
        final Authentication authentication = this.getAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((Details) authentication.getPrincipal()).getId();
    }

    @Override
    public String getCurrentUserName() {
        final Authentication authentication = this.getAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((Details) authentication.getPrincipal()).getUsername();
    }

    @Override
    public Locale getCurrentUserLocale() {
        final Authentication authentication = this.getAuthentication();
        if (authentication == null) {
            return null;
        }
        final String lang = ((Details) authentication.getPrincipal()).getLocale();

        return Locale.forLanguageTag(lang);
    }

}