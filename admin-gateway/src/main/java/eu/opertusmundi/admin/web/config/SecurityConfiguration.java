package eu.opertusmundi.admin.web.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import eu.opertusmundi.admin.web.logging.filter.MappedDiagnosticContextFilter;
import eu.opertusmundi.common.model.EnumAuthProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
	
	private static final String DEVELOPMENT_PROFILE = "development";
	
	private final Pattern csrfMethods = Pattern.compile("^(POST|PUT|DELETE)$");
	
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Value("${opertusmundi.authentication-providers:forms}")
    private List<EnumAuthProvider> authProviders;
    
    @Autowired
    @Qualifier("defaultUserDetailsService")
    UserDetailsService userDetailsService;

    @Autowired(required = false)
    ClientRegistrationRepository clientRegistrationRepository;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        authManagerBuilder.eraseCredentials(true);
        final AuthenticationManager authenticationManager = authManagerBuilder.build();

        http.authenticationManager(authenticationManager);
        
        http.authorizeRequests()
            // Configuration
            .antMatchers(
                "/action/configuration"
            ).permitAll()
            // Secured paths
            .antMatchers(
                "/logged-in", 
                "/logout",
                "/action/**"
            ).authenticated()
            // Restrict access to actuator endpoints (you may further restrict details via configuration)
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasIpAddress("127.0.0.1/8")
            // Public
            .antMatchers(
                // Permit all endpoints. Actions are secured using
                // annotations
                "/**"
            ).permitAll()
            // Secure any other path
            .anyRequest().authenticated();
        
        // Support form-based login

        if(this.authProviders.contains(EnumAuthProvider.Forms)) {
            http.formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=1")
                .defaultSuccessUrl("/logged-in", true)
                .usernameParameter("username")
                .passwordParameter("password");
        }

        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/logged-out")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .permitAll();
        
        // Configure CSRF
        http.csrf()
            .requireCsrfProtectionMatcher((HttpServletRequest req) -> {
                if (this.csrfMethods.matcher(req.getMethod()).matches()) {
                    // Disable CSRF when development profile is active
                    return !this.isDevelopmentProfileActive();
                }
                return false;
             });
        
        // Do not redirect unauthenticated requests (just respond with a status code)
        http.exceptionHandling()
            .authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        
        // OAuth2 configuration
        if(this.authProviders.contains(EnumAuthProvider.OpertusMundi)) {
            if (clientRegistrationRepository != null) {
                http.oauth2Login()
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(this.oidcUserService()))
                    .failureUrl("/login?error=2");
            }
        } else {
            http.oauth2Login().disable();
        }
        
        // Handle CORS (Fix security errors)
        //
        // See: https://docs.spring.io/spring-security/site/docs/current/reference/html5/#cors
        //
        // CORS must be processed before Spring Security because the pre-flight request
        // will not contain any cookies (i.e. the JSESSIONID). If the request does not
        // contain any cookies and Spring Security is first, the request will determine
        // the user is not authenticated (since there are no cookies in the request) and
        // reject it.
        if (this.isDevelopmentProfileActive()) {
            http.cors();
        }
        
        // Add filters
        http.addFilterAfter(new MappedDiagnosticContextFilter(), SwitchUserFilter.class);
        
        return http.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            final OidcUser oidcUser = delegate.loadUser(userRequest);
            final String email = (String) oidcUser.getAttributes().get("email");
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            return (OidcUser) userDetails;
        };
    }
    
    @Profile({"development"})
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    
    private boolean isDevelopmentProfileActive() {
        for (final String profileName : this.activeProfile.split(",")) {
            if (profileName.equalsIgnoreCase(DEVELOPMENT_PROFILE)) {
                return true;
            }
        }
        return false;
    }
    
}
