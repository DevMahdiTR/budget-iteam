package com.iteam.buget.core.mapper;

import com.iteam.buget.core.alert.Alert;
import com.iteam.buget.core.dto.response.AlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertMapper {
    private final CategoryMapper categoryMapper;

    public AlertResponse toResponse(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .budgetId(a.getBudget().getId())
                .budgetName(a.getBudget().getName())
                .category(categoryMapper.toResponse(a.getCategory()))
                .alertType(a.getAlertType())
                .message(a.getMessage())
                .read(a.isRead())
                .triggeredAt(a.getTriggeredAt())
                .build();
    }
}
