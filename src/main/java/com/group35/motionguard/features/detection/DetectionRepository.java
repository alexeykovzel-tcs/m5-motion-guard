package com.group35.motionguard.features.detection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;

public interface DetectionRepository extends JpaRepository<Detection, String> {

    @Query("SELECT d FROM Detection d WHERE d.pi.user = :username AND d.date > :from AND d.date < :to")
    Collection<DetectionView> findByUserForPeriod(String username, Date from, Date to);

    @Query("SELECT d FROM Detection d WHERE d.pi.holder.id = :username")
    Collection<DetectionView> findByHolder(String username);

    @Query("SELECT d FROM Detection d WHERE d.pi.id = :id")
    Collection<DetectionView> findByPi(String id);

    @Query("SELECT d.videoBase64 FROM Detection d WHERE d.id = :id")
    String getBase64ById(Long id);

    @Query("DELETE FROM Detection d WHERE d.id = : id")
    void deleteByLongId(Long id);
}
