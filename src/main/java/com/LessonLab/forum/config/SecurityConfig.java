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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.LessonLab.forum.config.filters.CustomAuthenticationFilter;
import com.LessonLab.forum.config.filters.CustomAuthorizationFilter;

import java.util.ArrayList;
import java.util.List;

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
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        configureEndpoints(http);

        // Custom Authentication and Authorization Filters
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                authManagerBuilder.getOrBuild());
        customAuthenticationFilter.setFilterProcessesUrl("/api/users/login");

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void configureEndpoints(HttpSecurity http) throws Exception {
        List<EndpointConfig> endpointConfigs = getEndpointConfigs();

        http.authorizeHttpRequests(auth -> {
            // First, permit all for Swagger UI and other public endpoints
            auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
            auth.requestMatchers("/api/users/login", "/api/users/register-user").permitAll();

            // Then, configure other endpoints
            for (EndpointConfig config : endpointConfigs) {
                if (config.getHttpMethod() != null) {
                    auth.requestMatchers(config.getHttpMethod(), config.getEndpoint())
                            .hasAnyAuthority(config.getRoles());
                } else {
                    auth.requestMatchers(config.getEndpoint()).hasAnyAuthority(config.getRoles());
                }
            }

            // Finally, require authentication for all other /api/** endpoints
            auth.requestMatchers("/api/**").authenticated();
        });
    }

    private List<EndpointConfig> getEndpointConfigs() {
        List<EndpointConfig> configs = new ArrayList<>();

        // Existing endpoints
        configs.add(new EndpointConfig("/api/users/login", HttpMethod.POST, "permitAll"));
        configs.add(new EndpointConfig("/api/users/register-user", HttpMethod.POST, "permitAll"));
        configs.add(new EndpointConfig("/swagger-ui/**", null, "permitAll"));
        configs.add(new EndpointConfig("/v3/api-docs/**", null, "permitAll"));
        configs.add(new EndpointConfig("/swagger-ui-lesson-lab.html", null, "permitAll"));

        // Content-related endpoints
        addContentEndpoints(configs);

        // User-related endpoints
        addUserEndpoints(configs);

        // Thread-related endpoints
        addThreadEndpoints(configs);

        // Comment-related endpoints
        addCommentEndpoints(configs);

        // Role-related endpoints
        addRoleEndpoints(configs);

        // Post-related endpoints
        addPostEndpoints(configs);

        return configs;
    }

    private void addContentEndpoints(List<EndpointConfig> configs) {
        String[] userRoles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR" };
        String[] adminModRoles = { "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/{contentType}/{id}", HttpMethod.PUT, userRoles));
        configs.add(new EndpointConfig("/api/{contentType}/get-content-by-id/{id}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/search/{contentType}", HttpMethod.GET, userRoles));
        configs.add(new EndpointConfig("/api/recent/{contentType}", HttpMethod.GET, userRoles));
        configs.add(new EndpointConfig("/api/user/{contentType}/get-paged-content-by-user/{userId}", HttpMethod.GET,
                adminModRoles));
        configs.add(new EndpointConfig("/api/created-at-between/{contentType}", HttpMethod.GET, userRoles));
        configs.add(new EndpointConfig("/api/content-containing/{contentType}", HttpMethod.GET, userRoles));
        configs.add(
                new EndpointConfig("/api/{contentType}/delete-content-by-id/{id}", HttpMethod.DELETE, adminModRoles));
        configs.add(new EndpointConfig("/api/list-all-content-of-type/{contentType}", HttpMethod.GET, userRoles));
        configs.add(new EndpointConfig("/api/{contentType}/{contentId}/vote", HttpMethod.POST, userRoles));
    }

    private void addUserEndpoints(List<EndpointConfig> configs) {
        String[] adminModRoles = { "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/users/get-user-by-id/{id}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/users/get-user-by-username/{username}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/users/status/{status}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/users/account-status/{accountStatus}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/users/{id}", HttpMethod.DELETE, adminModRoles));
        configs.add(new EndpointConfig("/api/users/username/{username}", HttpMethod.DELETE, adminModRoles));
    }

    private void addThreadEndpoints(List<EndpointConfig> configs) {
        String[] userRoles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/threads", HttpMethod.POST, userRoles));
        configs.add(new EndpointConfig("/api/threads/{id}", HttpMethod.PUT, userRoles));
        configs.add(new EndpointConfig("/api/threads/title/{title}", HttpMethod.GET, userRoles));
        configs.add(new EndpointConfig("/api/threads/description/{description}", HttpMethod.GET, userRoles));
    }

    private void addPostEndpoints(List<EndpointConfig> configs) {
        String[] userRoles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/posts/add-post-to-thread", HttpMethod.POST, userRoles));
        configs.add(new EndpointConfig("/api/posts/most-commented-posts", HttpMethod.GET, userRoles));
    }

    private void addCommentEndpoints(List<EndpointConfig> configs) {
        String[] userRoles = { "ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/comments/add-comment-to-post", HttpMethod.POST, userRoles));
    }

    private void addRoleEndpoints(List<EndpointConfig> configs) {
        String[] adminModRoles = { "ROLE_ADMIN", "ROLE_MODERATOR" };

        configs.add(new EndpointConfig("/api/roles/add-role-type", HttpMethod.POST, adminModRoles));
        configs.add(new EndpointConfig("/api/roles/roles/add-role-to-user", HttpMethod.POST, adminModRoles));
        configs.add(new EndpointConfig("/api/roles/get-user-by-role/{role}", HttpMethod.GET, adminModRoles));
        configs.add(new EndpointConfig("/api/roles/get-all-role-types", HttpMethod.GET, adminModRoles));
    }

    private static class EndpointConfig {
        private final String endpoint;
        private final HttpMethod httpMethod;
        private final String[] roles;

        public EndpointConfig(String endpoint, HttpMethod httpMethod, String... roles) {
            this.endpoint = endpoint;
            this.httpMethod = httpMethod;
            this.roles = roles;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public String[] getRoles() {
            return roles;
        }
    }
}