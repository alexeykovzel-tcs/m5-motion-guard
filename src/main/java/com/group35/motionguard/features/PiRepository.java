package com.group35.motionguard.features;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PiRepository extends JpaRepository<RaspberryPi, String> {

    RaspberryPi findByActivationCode(String code);

    @Query("SELECT pi.id FROM RaspberryPi pi WHERE pi.holder.user.username = :username")
    List<String> findIdsByHolder(String username);

    @Query("SELECT pi FROM RaspberryPi pi WHERE pi.holder.user.username = :username")
    List<RaspberryPi> findPisByHolder(String username);

    @Query("SELECT COUNT(pi) > 0 FROM RaspberryPi pi WHERE pi.holder.id = :holderId AND pi.id = :id")
    boolean isHolder(String id, String holderId);
}
