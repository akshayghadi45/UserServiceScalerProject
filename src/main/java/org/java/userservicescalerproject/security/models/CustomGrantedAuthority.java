package org.java.userservicescalerproject.security.models;

import org.java.userservicescalerproject.models.Role;
import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority {

    private final String authority;

    public CustomGrantedAuthority(Role role) {
       this.authority = role.getValue();
    }

    @Override
    public String getAuthority() {
        return "";
    }
}
