package com.LessonLab.forum.config.filters;

import com.LessonLab.forum.Models.User;
import com.LessonLab.forum.Services.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * CustomAuthorizationFilter is an implementation of OncePerRequestFilter to
 * handle
 * authorization of a user to access the API endpoints.
 */
@Component
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    /**
     * The method doFilterInternal will handle the authorization of a user to access
     * the API endpoints.
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException if there is a servlet related error
     * @throws IOException      if there is an Input/Output error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/api/users/login") || path.equals("/api/users/register-user") || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui-lesson-lab")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String username = decodedJWT.getSubject();

                // Fetch user here
                // UserService userService =
                // ApplicationContextProvider.getBean(UserService.class);
                User user = userService.getUser(username);

                if (user == null) {
                    throw new UsernameNotFoundException("User not found");
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                log.error("Error logging in: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
