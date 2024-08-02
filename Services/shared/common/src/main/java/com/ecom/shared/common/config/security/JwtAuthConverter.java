package com.ecom.shared.common.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String ROLE = "ROLE_";
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                jwt.getClaim( JwtClaimNames.SUB)
        );
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        if (!jwt.hasClaim(REALM_ACCESS) ) {
            return Set.of();
        }
        Map<String, Object> resourceAccess = jwt.getClaim(REALM_ACCESS);
        List<String> roles = (List) resourceAccess.get(ROLES);
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(ROLE + role))
                .collect(Collectors.toSet());
    }
}