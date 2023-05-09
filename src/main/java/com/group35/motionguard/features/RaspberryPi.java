package com.group35.motionguard.features;

import com.group35.motionguard.features.account.User;
import com.group35.motionguard.features.camera.Frame;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pis")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"user", "holder", "frames"})
@ToString(exclude = {"user", "holder", "frames"})
public class RaspberryPi {

    @Id
    @Column(name = "id")
    private String id;

    @MapsId
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "holder_id")
    private PiHolder holder;

    @Column(nullable = false)
    private String activationCode;

    @OneToMany(mappedBy = "pi")
    private List<Frame> frames;

    @Column(nullable = false)
    private String location;

    public RaspberryPi(User user, String activationCode, String location) {
        this(user, activationCode);
        this.location = location;
    }

    public RaspberryPi(User user, String activationCode) {
        this.user = user;
        this.activationCode = activationCode;
    }
}
