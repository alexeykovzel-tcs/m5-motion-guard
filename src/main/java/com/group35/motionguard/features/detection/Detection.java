package com.group35.motionguard.features.detection;

import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.camera.Frame;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "detections")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"pi", "frame"})
@ToString(exclude = {"pi", "frame"})
public class Detection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pi_id")
    private RaspberryPi pi;

    @OneToOne
    private Frame frame;

    @Column
    private Date date;

    @Column(columnDefinition = "text")
    private String videoBase64;

    public Detection(RaspberryPi pi) {
        this.pi = pi;
        this.date = new Date();
    }

    public Detection(RaspberryPi pi, Frame frame) {
        this(pi);
        this.frame = frame;
    }
}
