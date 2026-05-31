package com.iteam.buget.core.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deletionRequested = true")
    List<User> findAllDeletionRequests();

    @Query("SELECT u FROM User u WHERE u.accountValidated = false")
    List<User> findAllPendingValidation();
}
