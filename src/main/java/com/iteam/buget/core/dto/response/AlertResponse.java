package com.iteam.buget.core.dto.response;

import com.iteam.buget.core.alert.AlertType;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class AlertResponse {
    private Long id;
    private Long budgetId;
    private String budgetName;
    private CategoryResponse category;
    private AlertType alertType;
    private String message;
    private boolean read;
    private LocalDateTime triggeredAt;
}
