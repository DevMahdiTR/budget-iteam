package com.iteam.buget.core.token;


import com.iteam.buget.core.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "tokens")
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @OneToOne
    private User user;

    @PrePersist void prePersist() { this.issuedAt = LocalDateTime.now(); }

}
