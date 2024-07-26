package kr.co.tetrips.gatewayservice.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PrincipalUserDetails implements UserDetails, OAuth2User {
  private User user;
  private Map<String, Object> attributes;

  public PrincipalUserDetails(User user) {
    this.user = user;
  }

  public PrincipalUserDetails(User user, Map<String, Object> attributes) {
    this.user = user;
    this.attributes = attributes;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return attributes.get(user.getRegistration().name()).toString();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getRole().stream().map(i -> new SimpleGrantedAuthority(i.name())).toList();
  }
  @Override
  public String getPassword() {
    return null;
  }
  @Override
  public String getUsername() {
    return user.getEmail();
  }
}