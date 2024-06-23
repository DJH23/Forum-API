package com.LessonLab.forum.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.LessonLab.forum.config.filters.CustomAuthenticationFilter;
import com.LessonLab.forum.config.filters.CustomAuthorizationFilter;

import static org.springframework.http.HttpMethod.*;

/**
 * This is the main configuration class for security in the application. It
 * enables web security,
 * sets up the password encoder, and sets up the security filter chain.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthorizationFilter customAuthorizationFilter() throws Exception {
        return new CustomAuthorizationFilter();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/register-user", "/swagger-ui/**", "/v3/api-docs/**",
                        "/swagger-ui-lesson-lab.html")
                .permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/{contentType}/{id}").hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/{contentType}/get-content-by-id/{id}")
                .hasAnyRole("ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/search/{contentType}").hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/recent/{contentType}").hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/user/{contentType}/get-paged-content-by-user/{userId}")
                .hasAnyRole("ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/created-at-between/{contentType}")
                .hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/content-containing/{contentType}")
                .hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/users/username/{username}")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/{contentType}/delete-content-by-id/{id}")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR")
                .requestMatchers(HttpMethod.GET, "/api/list-all-content-of-type/{contentType}")
                .hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.POST, "/api/{contentType}/{contentId}/vote")
                .hasAnyRole("USER", "ADMIN", "MODERATOR")
                .requestMatchers("/api/**").authenticated());

        // Custom Authentication and Authorization Filters
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                authManagerBuilder.getOrBuild());
        customAuthenticationFilter.setFilterProcessesUrl("/api/users/login");

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * @Bean
     * public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
     * DefaultWebSecurityExpressionHandler handler = new
     * DefaultWebSecurityExpressionHandler();
     * handler.setDefaultRolePrefix(""); // No 'ROLE_' prefix
     * return handler;
     * }
     */

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        return new DefaultWebSecurityExpressionHandler();
    }
}
