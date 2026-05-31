package com.iteam.buget.core.budget;

import com.iteam.buget.core.budget.*;
import com.iteam.buget.core.category.Category;
import com.iteam.buget.core.category.CategoryRepository;
import com.iteam.buget.core.dto.request.AddMemberRequest;
import com.iteam.buget.core.dto.request.BudgetRequest;
import com.iteam.buget.core.dto.request.CategoryLimitRequest;
import com.iteam.buget.core.dto.response.BudgetMemberResponse;
import com.iteam.buget.core.dto.response.BudgetResponse;
import com.iteam.buget.core.dto.response.CategoryLimitResponse;
import com.iteam.buget.core.mapper.BudgetMapper;
import com.iteam.buget.core.role.MemberRole;
import com.iteam.buget.core.user.User;
import com.iteam.buget.core.user.UserRepository;
import com.iteam.buget.exception.AccessDeniedException;
import com.iteam.buget.exception.AppException;
import com.iteam.buget.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMemberRepository memberRepository;
    private final BudgetCategoryLimitRepository limitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    public List<BudgetResponse> getMyBudgets(User user) {
        return budgetRepository.findAllForUser(user)
                .stream().map(budgetMapper::toResponse).toList();
    }

    public BudgetResponse getById(User user, Long id) {
        Budget budget = findAndCheckAccess(user, id);
        return budgetMapper.toResponse(budget);
    }

    @Transactional
    public BudgetResponse create(User user, BudgetRequest request) {
        if (request.getPeriod().name().equals("CUSTOM") &&
                (request.getStartDate() == null || request.getEndDate() == null)) {
            throw new AppException("Start and end dates are required for CUSTOM period", HttpStatus.BAD_REQUEST);
        }
        Budget budget = Budget.builder()
                .name(request.getName())
                .description(request.getDescription())
                .globalCeiling(request.getGlobalCeiling())
                .period(request.getPeriod())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .shared(request.isShared())
                .owner(user)
                .build();

        Budget saved = budgetRepository.save(budget);

        // Owner is also a member
        BudgetMember ownerMember = BudgetMember.builder()
                .budget(saved)
                .user(user)
                .memberRole(MemberRole.OWNER)
                .build();
        memberRepository.save(ownerMember);

        return budgetMapper.toResponse(saved);
    }

    @Transactional
    public BudgetResponse update(User user, Long id, BudgetRequest request) {
        Budget budget = findAndCheckOwner(user, id);
        budget.setName(request.getName());
        budget.setDescription(request.getDescription());
        budget.setGlobalCeiling(request.getGlobalCeiling());
        budget.setPeriod(request.getPeriod());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setShared(request.isShared());
        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(User user, Long id) {
        Budget budget = findAndCheckOwner(user, id);
        budgetRepository.delete(budget);
    }

    // ── Members ──────────────────────────────────────────────────────────────

    @Transactional
    public BudgetMemberResponse addMember(User owner, Long budgetId, AddMemberRequest request) {
        Budget budget = findAndCheckOwner(owner, budgetId);
        if (!budget.isShared()) {
            throw new AppException("Cannot add members to a personal budget", HttpStatus.BAD_REQUEST);
        }
        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found with email: " + request.getEmail(), HttpStatus.NOT_FOUND));

        if (memberRepository.existsByBudgetIdAndUser(budgetId, newMember)) {
            throw new AppException("User is already a member of this budget", HttpStatus.CONFLICT);
        }
        BudgetMember member = BudgetMember.builder()
                .budget(budget)
                .user(newMember)
                .memberRole(MemberRole.MEMBER)
                .build();
        return budgetMapper.toMemberResponse(memberRepository.save(member));
    }

    @Transactional
    public void removeMember(User owner, Long budgetId, Long memberId) {
        findAndCheckOwner(owner, budgetId);
        BudgetMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
        if (member.getMemberRole() == MemberRole.OWNER) {
            throw new AppException("Cannot remove the budget owner", HttpStatus.BAD_REQUEST);
        }
        memberRepository.delete(member);
    }

    // ── Category limits ──────────────────────────────────────────────────────

    @Transactional
    public CategoryLimitResponse setCategoryLimit(User user, Long budgetId, CategoryLimitRequest request) {
        Budget budget = findAndCheckOwner(user, budgetId);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        BudgetCategoryLimit limit = limitRepository
                .findByBudgetIdAndCategoryId(budgetId, request.getCategoryId())
                .orElse(BudgetCategoryLimit.builder().budget(budget).category(category).build());

        limit.setCeiling(request.getCeiling());
        return budgetMapper.toCategoryLimitResponse(limitRepository.save(limit), BigDecimal.ZERO);
    }

    @Transactional
    public void deleteCategoryLimit(User user, Long budgetId, Long limitId) {
        findAndCheckOwner(user, budgetId);
        BudgetCategoryLimit limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryLimit", limitId));
        limitRepository.delete(limit);
    }

    // ── Admin: all shared budgets ────────────────────────────────────────────

    public List<BudgetResponse> getAllShared() {
        return budgetRepository.findBySharedTrue().stream().map(budgetMapper::toResponse).toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public Budget findAndCheckAccess(User user, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        boolean isOwner = budget.getOwner().getId().equals(user.getId());
        boolean isMember = memberRepository.existsByBudgetIdAndUser(budgetId, user);
        if (!isOwner && !isMember) {
            throw new AccessDeniedException("You are not a member of this budget");
        }
        return budget;
    }

    private Budget findAndCheckOwner(User user, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        if (!budget.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the budget owner can perform this action");
        }
        return budget;
    }
}