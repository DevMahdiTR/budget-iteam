package com.iteam.buget.core.dto.response;

import com.iteam.buget.core.role.MemberRole;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class BudgetMemberResponse {
    private Long id;
    private UserResponse user;
    private MemberRole memberRole;
    private LocalDateTime joinedAt;
}
