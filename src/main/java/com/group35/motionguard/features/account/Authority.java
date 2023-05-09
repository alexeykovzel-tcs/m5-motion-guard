package com.group35.motionguard.features.account;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public enum Authority implements GrantedAuthority {
    ADMIN,
    PI_HOLDER,
    PI;

    public List<Authority> single() {
        return List.of(this);
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
