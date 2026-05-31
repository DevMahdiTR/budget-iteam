package com.iteam.buget.core.controller;


import com.iteam.buget.core.dashboard.DashboardService;
import com.iteam.buget.core.dto.response.DashboardResponse;
import com.iteam.buget.core.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated financial statistics and KPIs per budget")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get full dashboard for a budget",
            description = """
            Returns aggregated statistics for the given budget:
            - Total income and expenses
            - Available balance
            - Budget consumed percentage and status (CONTROLLED / NEAR_LIMIT / EXCEEDED)
            - Savings rate and expense rate
            - Expense breakdown by category (pie chart data)
            - Monthly expense trend (line/bar chart data)
            - Per-category ceiling progress
            - Unread alerts
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard data returned",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{budgetId}")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal User user,
            @PathVariable Long budgetId) {
        return ResponseEntity.ok(dashboardService.getDashboard(user, budgetId));
    }
}