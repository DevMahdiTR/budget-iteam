package com.iteam.buget.core.controller;


import com.iteam.buget.core.dto.request.CommentRequest;
import com.iteam.buget.core.dto.request.TransactionRequest;
import com.iteam.buget.core.dto.response.CommentResponse;
import com.iteam.buget.core.dto.response.TransactionResponse;
import com.iteam.buget.core.transaction.TransactionService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Income and expense transactions, plus per-transaction comments")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    // ── Transactions ──────────────────────────────────────────────────────────

    @Operation(summary = "List all transactions for a budget",
            description = "Returns every transaction (income and expense) recorded in the given budget. Caller must be a budget member.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction list returned"),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<TransactionResponse>> getByBudget(
            @AuthenticationPrincipal User user,
            @PathVariable Long budgetId) {
        return ResponseEntity.ok(transactionService.getByBudget(user, budgetId));
    }

    @Operation(summary = "Get a single transaction by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction returned",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(user, id));
    }

    @Operation(summary = "Create a new transaction (income or expense)",
            description = "The transaction is linked to a budget and a category. Automatically triggers alert evaluation after creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Budget or category not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(user, request));
    }

    @Operation(summary = "Update a transaction",
            description = "Only the user who created the transaction can update it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not the creator of this transaction",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(user, id, request));
    }

    @Operation(summary = "Delete a transaction",
            description = "Only the creator of the transaction can delete it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted"),
            @ApiResponse(responseCode = "403", description = "Not the creator of this transaction",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        transactionService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    @Operation(summary = "List comments on a transaction",
            description = "Caller must be a member of the budget that contains the transaction.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment list returned"),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getComments(user, id));
    }

    @Operation(summary = "Add a comment to a transaction",
            description = "Any budget member can comment on any transaction within that budget.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment added",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not a member of this budget",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addComment(user, id, request));
    }

    @Operation(summary = "Delete a comment",
            description = "Only the author of the comment can delete it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted"),
            @ApiResponse(responseCode = "403", description = "Not the author of this comment",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable Long commentId) {
        transactionService.deleteComment(user, commentId);
        return ResponseEntity.noContent().build();
    }
}