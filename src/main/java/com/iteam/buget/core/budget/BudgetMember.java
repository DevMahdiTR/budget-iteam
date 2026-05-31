package com.iteam.buget.core.budget;


import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.role.MemberRole;
import com.iteam.buget.core.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BudgetMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist void prePersist() { this.joinedAt = LocalDateTime.now(); }
}