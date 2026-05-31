package com.iteam.buget.core.role;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "role-name", nullable = false, unique = true)
    private RoleName roleName;
}
