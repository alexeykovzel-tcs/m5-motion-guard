package com.group35.motionguard.features.account;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"authorities"})
@ToString(exclude = "authorities")
public class User {

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email = "";

    @Column
    private String phone = "";

    @Column
    private boolean isEnabled2FA = false;

    @Column
    private boolean isVerified = true;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "code_id", referencedColumnName = "id")
    private VerificationCode code;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<Authority> authorities;

    public User(String username, String password, Collection<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
}
