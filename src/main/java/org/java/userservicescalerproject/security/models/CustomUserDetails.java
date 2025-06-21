package org.java.userservicescalerproject.security.models;

import org.java.userservicescalerproject.models.Role;
import org.java.userservicescalerproject.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {


    private final String username;
    private final String password;
    private final Boolean accountNonExpired;
    private final Boolean credentialsNonExpired;
    private final Boolean accountNonLocked;
    private final Boolean enabled;
    private final List<CustomGrantedAuthority> authorities;

    public CustomUserDetails(User user) {

        this.username = user.getEmail();
        this.password = user.getPassword();
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
        this.enabled = true;
        this.authorities = new ArrayList<CustomGrantedAuthority>();
        for(Role role : user.getRoles()) {
            authorities.add(new CustomGrantedAuthority(role));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
