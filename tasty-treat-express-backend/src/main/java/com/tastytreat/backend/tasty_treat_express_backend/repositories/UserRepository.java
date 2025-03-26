package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tastytreat.backend.tasty_treat_express_backend.models.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	
	boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE users AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.address = :address WHERE u.id = :userId")
    void updateUserAddress(@Param("userId") long userId, @Param("address") String address);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updateUserPassword(@Param("userId") long userId, @Param("password") String password);


}
