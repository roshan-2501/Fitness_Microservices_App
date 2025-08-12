package com.fitness.userservice.repository;

import com.fitness.userservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(@NotNull(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);

    Boolean existsByKeyCloakId(String userId);

    User findByEmail(@NotNull(message = "Email cannot be empty") @Email(message = "Invalid email format") String email);
}
