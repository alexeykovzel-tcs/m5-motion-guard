package com.group35.motionguard.features.account;

import lombok.Data;

import java.util.List;

@Data
public class Credentials {
    private final String username;
    private final String password;

    public User buildUser(Authority... authorities) {
        return new User(username, encodePassword(), List.of(authorities));
    }

    public String encodePassword() {
        return new PasswordEncoder().encode(password);
    }
}
