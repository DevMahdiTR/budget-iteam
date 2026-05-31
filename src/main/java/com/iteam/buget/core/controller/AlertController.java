package com.iteam.buget.core.controller;

import com.iteam.buget.core.alert.AlertService;
import com.iteam.buget.core.dto.response.AlertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Budget threshold and overspend alerts")
@SecurityRequirement(name = "bearerAuth")
public class AlertController {

    private final AlertService alertService;

    @Operation(summary = "List all alerts for a budget",
            description = "Returns all alerts (read and unread) triggered for the given budget, including per-category alerts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<AlertResponse>> getAlertsForBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(alertService.getAlertsForBudget(budgetId));
    }

    @Operation(summary = "List unread alerts for a budget",
            description = "Useful for notification badges — returns only alerts not yet marked as read.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unread alert list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/budget/{budgetId}/unread")
    public ResponseEntity<List<AlertResponse>> getUnreadAlerts(@PathVariable Long budgetId) {
        return ResponseEntity.ok(alertService.getUnreadAlerts(budgetId));
    }

    @Operation(summary = "Mark an alert as read",
            description = "Dismisses the alert so it no longer appears in unread counts.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alert marked as read"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Alert not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        alertService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}