package com.taskmaster_springboot.repository;

import com.taskmaster_springboot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    @Query("SELECT u FROM users u WHERE u.email = :emailOrUsername OR u.username = :emailOrUsername")
    Optional<Users> findByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);

    Users findByEmail(String email);
    boolean existsByEmail(String email);

    Users findByPasswordResetToken(String token);

}
