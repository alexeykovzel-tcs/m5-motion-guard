package com.group35.motionguard.features.camera;

import com.group35.motionguard.features.RaspberryPi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface FrameRepository extends JpaRepository<Frame, Long> {

    Frame findFirstByPiOrderByDateDesc(RaspberryPi pi);

    List<Frame> findFramesByPiOrderByDateDesc(RaspberryPi pi);

    @Query("SELECT f.pi.id AS piId, MAX(f.date), f.base64 FROM Frame f WHERE f.pi.user.username = :username GROUP BY f.pi")
    List<FrameView> findLastByUsername(String username);


    @Modifying
    @Query("DELETE FROM Frame f WHERE f.date < :date AND f.isDetection = false")
    void deleteNotDetectionsUntil(Date date);
}
