package com.guido.guzman.msv.oauth.services;

import com.guido.guzman.msv.oauth.entities.User;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private WebClient client;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private Tracer tracer;

    public UserService(WebClient client, Tracer tracer) {
        this.client = client;
        this.tracer = tracer;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Calling login process UserService::loadUserByUsername with username: {}", username);
        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            User user = client.get().uri("/username/{username}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            List<GrantedAuthority> roles = user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
            logger.info("Login successful by username: {}", username);
            tracer.currentSpan().tag("success.login", String.format("Login successful by username: '%s'", username));
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEnabled(),
                    true,
                    true,
                    true,
                    roles
            );
        } catch (WebClientResponseException e) {
            String error = getErrorMessage(username);
            logger.error(error);
            tracer.currentSpan().tag("error.login.message", error + " - " + e.getMessage());
            throw new UsernameNotFoundException(getErrorMessage(username));
        }
    }

    private String getErrorMessage(String username) {
        return String.format("Error en el login, no existe el user '%s' en el sistema", username);
    }
}
