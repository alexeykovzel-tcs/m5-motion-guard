package com.group35.motionguard.features;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PiHolderRepository extends JpaRepository<PiHolder, String> {
}
