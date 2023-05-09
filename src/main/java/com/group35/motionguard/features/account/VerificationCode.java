package com.group35.motionguard.features.account;

import com.group35.motionguard.utils.StringUtils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "codes")
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "code")
    private User user;

    @Column
    private String symbols;

    @Column
    private Date date;

    public VerificationCode() {
        this.date = new Date();
        this.symbols = StringUtils.generate(8);
    }
}
