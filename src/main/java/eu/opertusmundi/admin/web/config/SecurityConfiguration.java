package eu.opertusmundi.admin.web.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import eu.opertusmundi.admin.web.logging.filter.MappedDiagnosticContextFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	
	private static final String DEVELOPMENT_PROFILE = "development";
	
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    
    @Autowired
    @Qualifier("defaultUserDetailsService")
    UserDetailsService userService;

    @Override
    public void configure(WebSecurity security) throws Exception
    {
        security.ignoring()
            .antMatchers(
                "/i18n/**", "/images/**", "/static/**", "/i18n/**" 
			);
    }
     
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception
    {
        // Which authentication providers are to be used?

        builder.userDetailsService(userService)
            .passwordEncoder(new BCryptPasswordEncoder());

        builder.eraseCredentials(true);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        // Authorize requests:

        security.authorizeRequests()
        	// Public paths
            .antMatchers(
                    "/", "/index",
                    "/login", "/logged-out")
                .permitAll()
            // Secured paths
            .antMatchers(
                    "/logged-in", "/logout",
                    "/action/**")
                .authenticated()
            // Restrict access to actuator to system administrators only
            .requestMatchers(EndpointRequest.toAnyEndpoint())
            	.hasRole("ADMIN_SYSTEM");

        // Support form-based login

        security.formLogin()
            .loginPage("/login")
            .failureUrl("/login?error")
            .defaultSuccessUrl("/logged-in", true)
            .usernameParameter("username")
            .passwordParameter("password");

        security.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/logged-out")
            .invalidateHttpSession(true);

        // Configure CSRF
        
        security.csrf()
            .requireCsrfProtectionMatcher((HttpServletRequest req) -> {
                String method = req.getMethod();
                String path = req.getServletPath();
                if (path.startsWith("/api/")) {
                    return false; // exclude Rest API
                }
                if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
                	// Disable CSRF when development profile is active
                    return !this.isDevelopmentProfileActive();
                }
                return false;
             });

        // Do not redirect unauthenticated requests (just respond with a status code)

        security.exceptionHandling()
            .authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

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
			security.cors();
		}
        
        // Add filters

        security.addFilterAfter(
            new MappedDiagnosticContextFilter(), SwitchUserFilter.class);
    }
    
    @Profile({"development"})
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowCredentials(true);
        
        configuration.setAllowedOrigins(Arrays.asList("*"));
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
