package com.group35.motionguard.config;

import com.group35.motionguard.features.PiHolder;
import com.group35.motionguard.features.PiHolderRepository;
import com.group35.motionguard.features.PiRepository;
import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.account.Authority;
import com.group35.motionguard.features.account.PasswordEncoder;
import com.group35.motionguard.features.account.User;
import com.group35.motionguard.features.account.UserRepository;
import com.group35.motionguard.features.camera.FrameRepository;
import com.group35.motionguard.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Profile({"dev"})
public class DatabaseConfig {
    private final PiHolderRepository piHolderRepository;
    private final FrameRepository frameRepository;
    private final UserRepository userRepository;
    private final PiRepository piRepository;

    @Bean
    public void deleteOldFrames() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Date minDate = DateUtils.shiftMinutes(new Date(), -1);
            frameRepository.deleteNotDetectionsUntil(minDate);
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Bean
    public void initUsers() {
        if (userRepository.count() != 0) return;
        PasswordEncoder encoder = new PasswordEncoder();

        User piUser1 = new User("1", encoder.encode("123"), Authority.PI.single());
        User piUser2 = new User("2", encoder.encode("123"), Authority.PI.single());
        RaspberryPi pi1 = new RaspberryPi(piUser1, "777", "front door");
        RaspberryPi pi2 = new RaspberryPi(piUser2, "888", "back door");

        User admin = new User("admin", encoder.encode("123"), List.of(Authority.PI_HOLDER, Authority.ADMIN));
        User piUser = new User("user", encoder.encode("123"), Authority.PI_HOLDER.single());
        PiHolder piHolder = new PiHolder(piUser, List.of(pi1));

        pi1.setHolder(piHolder);
        piHolderRepository.save(piHolder);
        userRepository.save(admin);
        piRepository.save(pi2);
    }
}
