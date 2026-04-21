package com.limasantos.pharmacy.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    @Autowired
    private SecurityFilter securityFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorize ->
                        authorize

                                .requestMatchers(SWAGGER_WHITELIST).permitAll()

                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/auth/login",
                                        "/auth/register"
                                ).permitAll()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/product/create",
                                        "/inventory/create",
                                        "/sales/create",
                                        "/customer/create",
                                        "/financial/create",
                                        "/suppliers/create"
                                ).hasRole("ADMIN")

                                .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
