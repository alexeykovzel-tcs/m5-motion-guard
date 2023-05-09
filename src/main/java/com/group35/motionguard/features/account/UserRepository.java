package com.group35.motionguard.features.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(String username, String password);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.username = :username")
    void updateEmail(String username, String email);

    @Modifying
    @Query("UPDATE User u SET u.phone = :phone WHERE u.username = :username")
    void updatePhone(String username, String phone);
}
