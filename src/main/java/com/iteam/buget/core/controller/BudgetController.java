package com.iteam.buget.core.controller;



import com.iteam.buget.core.budget.BudgetService;
import com.iteam.buget.core.dto.request.AddMemberRequest;
import com.iteam.buget.core.dto.request.BudgetRequest;
import com.iteam.buget.core.dto.request.CategoryLimitRequest;
import com.iteam.buget.core.dto.response.BudgetMemberResponse;
import com.iteam.buget.core.dto.response.BudgetResponse;
import com.iteam.buget.core.dto.response.CategoryLimitResponse;
import com.iteam.buget.core.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Create and manage personal or shared budgets, members and category ceilings")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @Operation(summary = "List all budgets for the current user",
            description = "Returns budgets where the user is the owner or a member.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getMyBudgets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.getMyBudgets(user));
    }

    @Operation(summary = "Get a single budget by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget returned",
                    content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getById(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getById(user, id));
    }

    @Operation(summary = "Create a new budget",
            description = "Creates a personal or shared budget. For CUSTOM period, startDate and endDate are required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created",
                    content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    @Operation(summary = "Update a budget",
            description = "Only the budget owner can update it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated",
                    content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Only the owner can update",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.update(user, id, request));
    }

    @Operation(summary = "Delete a budget",
            description = "Only the budget owner can delete it. All associated transactions and alerts are also deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted"),
            @ApiResponse(responseCode = "403", description = "Only the owner can delete",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        budgetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    // ── Members ──────────────────────────────────────────────────────────────

    @Operation(summary = "Add a member to a shared budget",
            description = "Only the owner can add members. The budget must be marked as shared.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member added",
                    content = @Content(schema = @Schema(implementation = BudgetMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Budget is not shared or validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found by email",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "User is already a member",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/{id}/members")
    public ResponseEntity<BudgetMemberResponse> addMember(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.addMember(user, id, request));
    }

    @Operation(summary = "Remove a member from a shared budget",
            description = "Only the owner can remove members. The owner themselves cannot be removed.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member removed"),
            @ApiResponse(responseCode = "400", description = "Cannot remove the budget owner",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Only the owner can remove members",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable Long memberId) {
        budgetService.removeMember(user, id, memberId);
        return ResponseEntity.noContent().build();
    }

    // ── Category limits ──────────────────────────────────────────────────────

    @Operation(summary = "Set or update a per-category spending ceiling",
            description = "If a limit for the given category already exists it is updated; otherwise a new one is created. Owner only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category limit saved",
                    content = @Content(schema = @Schema(implementation = CategoryLimitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Only the owner can set limits",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget or category not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/{id}/category-limits")
    public ResponseEntity<CategoryLimitResponse> setCategoryLimit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CategoryLimitRequest request) {
        return ResponseEntity.ok(budgetService.setCategoryLimit(user, id, request));
    }

    @Operation(summary = "Remove a per-category spending ceiling")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category limit removed"),
            @ApiResponse(responseCode = "403", description = "Only the owner can remove limits",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Limit not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}/category-limits/{limitId}")
    public ResponseEntity<Void> deleteCategoryLimit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable Long limitId) {
        budgetService.deleteCategoryLimit(user, id, limitId);
        return ResponseEntity.noContent().build();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Operation(summary = "[Admin] List all shared budgets across the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared budget list returned"),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/admin/shared")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BudgetResponse>> getAllShared() {
        return ResponseEntity.ok(budgetService.getAllShared());
    }
}