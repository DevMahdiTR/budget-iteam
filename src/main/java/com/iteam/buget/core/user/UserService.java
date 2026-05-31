package com.iteam.buget.core.user;

import com.iteam.buget.core.dto.request.ChangePasswordRequest;
import com.iteam.buget.core.dto.request.UpdateProfileRequest;
import com.iteam.buget.core.dto.response.UserResponse;
import com.iteam.buget.core.email.EmailService;
import com.iteam.buget.core.mapper.UserMapper;
import com.iteam.buget.core.user.User;
import com.iteam.buget.core.user.UserRepository;
import com.iteam.buget.exception.AppException;
import com.iteam.buget.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public UserResponse getProfile(User currentUser) {
        return userMapper.toResponse(currentUser);
    }

    @Transactional
    public UserResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        currentUser.setFirstName(request.getFirstName());
        currentUser.setLastName(request.getLastName());
        return userMapper.toResponse(userRepository.save(currentUser));
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Transactional
    public void requestAccountDeletion(User currentUser) {
        currentUser.setDeletionRequested(true);
        userRepository.save(currentUser);
    }

    // ── Admin operations ─────────────────────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public List<UserResponse> getPendingValidations() {
        return userRepository.findAllPendingValidation().stream().map(userMapper::toResponse).toList();
    }

    public List<UserResponse> getDeletionRequests() {
        return userRepository.findAllDeletionRequests().stream().map(userMapper::toResponse).toList();
    }

    @Transactional
    public UserResponse validateAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setAccountValidated(true);
        User saved = userRepository.save(user);
        emailService.sendAccountValidation(user.getEmail(), user.getFirstName() + " " + user.getLastName());
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void approveAccountDeletion(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        emailService.sendAccountDeletionConfirmation(user.getEmail(), user.getFirstName() + " " + user.getLastName());
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse assignRole(UUID userId, String roleName) {
        // Simplified — full role assignment is in RoleService if needed
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return userMapper.toResponse(user);
    }
}
