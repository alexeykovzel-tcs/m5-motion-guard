package com.group35.motionguard.features.camera;

import com.group35.motionguard.features.RaspberryPi;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pi_frames")
@Getter
@Setter
@EqualsAndHashCode(exclude = "pi")
@ToString(exclude = "pi")
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "pi_id")
    private RaspberryPi pi;

    @Column
    private boolean isDetection;

    @Column(columnDefinition = "text")
    private String base64;

    @Column
    private Date date;
}
