package com.group35.motionguard.features;

import com.group35.motionguard.features.account.User;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "pi_holders")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"user", "pis"})
@ToString(exclude = {"user", "pis"})
public class PiHolder {

    @Id
    @Column(name = "user_id")
    private String id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "holder", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Collection<RaspberryPi> pis;

    public PiHolder(User user) {
        this.user = user;
    }

    public PiHolder(User user, Collection<RaspberryPi> pis) {
        this(user);
        this.pis = pis;
    }
}
