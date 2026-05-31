package com.iteam.buget.core.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Query("""
                SELECT t FROM Token t
                WHERE t.user.id = :userId
                  AND t.revoked = false
                  AND t.expired = false
            """)
    List<Token> findAllValidTokensByUser(UUID userId);
}

