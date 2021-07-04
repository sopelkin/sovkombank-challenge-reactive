package dev.softwarecats.scbchallenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/").permitAll()
            .anyExchange().authenticated()
            .and()
            .httpBasic()
            .and()
            .formLogin().disable()
            .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("{noop}password123")
                .roles("ADMIN")
                .build();
       return new MapReactiveUserDetailsService(user);
    }
}
