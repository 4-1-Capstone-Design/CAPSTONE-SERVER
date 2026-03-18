package com.capstone.global.security;


import com.capstone.domain.user.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserPrincipal implements UserDetails {

  private final Long userId;
  private final String email;
  private final Collection<? extends GrantedAuthority> authorities;

  public static UserPrincipal create(User user) {
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(user.getRole().getKey()));
    return UserPrincipal.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .authorities(authorities)
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return String.valueOf(userId);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}