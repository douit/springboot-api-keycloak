package com.example.demo.Config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.example.demo.ObjectToUrlEncodedConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

// import org.keycloak.adapters.KeycloakConfigResolver;
// import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
// import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
// import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
// import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
// import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
// import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
// import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
// import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
// import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
// import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
// import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityConfig extends KeycloakAuthenticationProvider {

  private final SimpleAuthorityMapper grantedAuthorityMapper;

  public SecurityConfig(SimpleAuthorityMapper grantedAuthorityMapper) {
    this.grantedAuthorityMapper = grantedAuthorityMapper;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.addAll(getKeycloakRealmRolesToAuthorities());
    return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), authorities);
  }

  private Collection<GrantedAuthority> getKeycloakRealmRolesToAuthorities() {
    AccessToken accessToken = getLoggedInKeycloakUserAccessToken();
    Set<String> realmRoles = accessToken.getRealmAccess().getRoles();
    return toGrantedAuthorities(realmRoles);
  }

  private Collection<GrantedAuthority> toGrantedAuthorities(Set<String> roles) {
    Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
    return grantedAuthorityMapper.mapAuthorities(authorities);
  }

  public KeycloakPrincipal<KeycloakSecurityContext> getLoggedInKeycloakUser() {
    return (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
  }

  public KeycloakSecurityContext getLoggedInKeycloakUserSecurityContext() {
    return getLoggedInKeycloakUser().getKeycloakSecurityContext();
  }

  public AccessToken getLoggedInKeycloakUserAccessToken() {
    return getLoggedInKeycloakUserSecurityContext().getToken();
  }

}
