package com.iteam.buget.core.controller;


import com.iteam.buget.core.dto.request.ChangePasswordRequest;
import com.iteam.buget.core.dto.request.UpdateProfileRequest;
import com.iteam.buget.core.dto.response.UserResponse;
import com.iteam.buget.core.user.User;
import com.iteam.buget.core.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management and admin user operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // ── Self ─────────────────────────────────────────────────────────────────

    @Operation(summary = "Get current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @Operation(summary = "Update current user's first and last name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/users/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @Operation(summary = "Change current user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Current password incorrect or validation error",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/users/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request account deletion",
            description = "Flags the account for deletion. An admin must approve it — the account is not deleted immediately.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Deletion request submitted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> requestDeletion(@AuthenticationPrincipal User user) {
        userService.requestAccountDeletion(user);
        return ResponseEntity.accepted().build();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Operation(summary = "[Admin] List all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list returned"),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "[Admin] List accounts pending validation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pending accounts returned"),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/admin/users/pending-validation")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getPendingValidations() {
        return ResponseEntity.ok(userService.getPendingValidations());
    }

    @Operation(summary = "[Admin] Validate a user account",
            description = "Marks the account as validated and sends a confirmation email to the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account validated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/admin/users/{id}/validate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> validateAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.validateAccount(id));
    }

    @Operation(summary = "[Admin] List accounts awaiting deletion approval")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deletion request list returned"),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/admin/users/deletion-requests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getDeletionRequests() {
        return ResponseEntity.ok(userService.getDeletionRequests());
    }

    @Operation(summary = "[Admin] Approve account deletion",
            description = "Permanently deletes the user and all their data after sending a confirmation email.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> approveAccountDeletion(@PathVariable UUID id) {
        userService.approveAccountDeletion(id);
        return ResponseEntity.noContent().build();
    }
}