package com.ecom.shared.common.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig  {

    private JwtAuthConverter jwtAuthConverter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->authorizationManagerRequestMatcherRegistry
                   .requestMatchers(HttpMethod.POST, "/auth/login", "/users/addUser").permitAll()
                   .requestMatchers(HttpMethod.GET, "/files/**", "/product","/app/started").permitAll()
                            .requestMatchers("/cart/**").hasRole("CART")
                                    .requestMatchers("/order/**").hasRole("ORDER")
                                    .requestMatchers("/products/**").hasRole("PRODUCT")
                                    .anyRequest().authenticated())
                   .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.
                    jwt (jwtSpec -> jwtSpec.jwtAuthenticationConverter(jwtAuthConverter)));


        //http.addFilterAfter(new HttpRequestFilter(), BasicAuthenticationFilter.class);
       // http.csrf(AbstractHttpConfigurer::disable);
        //http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));
      return   http.build();
    }

}