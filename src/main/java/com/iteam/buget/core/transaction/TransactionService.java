package com.iteam.buget.core.transaction;

import com.iteam.buget.core.alert.AlertService;
import com.iteam.buget.core.budget.BudgetService;
import com.iteam.buget.core.category.Category;
import com.iteam.buget.core.category.CategoryRepository;
import com.iteam.buget.core.comment.Comment;
import com.iteam.buget.core.comment.CommentRepository;
import com.iteam.buget.core.dto.request.CommentRequest;
import com.iteam.buget.core.dto.request.TransactionRequest;
import com.iteam.buget.core.dto.response.CommentResponse;
import com.iteam.buget.core.dto.response.TransactionResponse;
import com.iteam.buget.core.mapper.CommentMapper;
import com.iteam.buget.core.mapper.TransactionMapper;
import com.iteam.buget.core.transaction.Transaction;
import com.iteam.buget.core.transaction.TransactionRepository;
import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.user.User;
import com.iteam.buget.exception.AccessDeniedException;
import com.iteam.buget.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final BudgetService budgetService;
    private final AlertService alertService;
    private final TransactionMapper transactionMapper;
    private final CommentMapper commentMapper;

    public List<TransactionResponse> getByBudget(User user, Long budgetId) {
        budgetService.findAndCheckAccess(user, budgetId);
        return transactionRepository.findByBudgetId(budgetId)
                .stream().map(transactionMapper::toResponse).toList();
    }

    public TransactionResponse getById(User user, Long id) {
        Transaction t = findTransaction(id);
        budgetService.findAndCheckAccess(user, t.getBudget().getId());
        return transactionMapper.toResponse(t);
    }

    @Transactional
    public TransactionResponse create(User user, TransactionRequest request) {
        Budget budget = budgetService.findAndCheckAccess(user, request.getBudgetId());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .category(category)
                .budget(budget)
                .createdBy(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Trigger alert check asynchronously after saving
        alertService.checkBudgetAlerts(budget);

        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public TransactionResponse update(User user, Long id, TransactionRequest request) {
        Transaction transaction = findTransaction(id);
        checkOwnership(user, transaction);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);

        Transaction saved = transactionRepository.save(transaction);
        alertService.checkBudgetAlerts(transaction.getBudget());
        return transactionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(User user, Long id) {
        Transaction transaction = findTransaction(id);
        checkOwnership(user, transaction);
        transactionRepository.delete(transaction);
    }

    // ── Comments ─────────────────────────────────────────────────────────────

    public List<CommentResponse> getComments(User user, Long transactionId) {
        Transaction transaction = findTransaction(transactionId);
        budgetService.findAndCheckAccess(user, transaction.getBudget().getId());
        return commentRepository.findByTransactionId(transactionId)
                .stream().map(commentMapper::toResponse).toList();
    }

    @Transactional
    public CommentResponse addComment(User user, Long transactionId, CommentRequest request) {
        Transaction transaction = findTransaction(transactionId);
        budgetService.findAndCheckAccess(user, transaction.getBudget().getId());

        Comment comment = Comment.builder()
                .content(request.getContent())
                .transaction(transaction)
                .author(user)
                .build();
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Transaction findTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    private void checkOwnership(User user, Transaction transaction) {
        if (!transaction.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only modify your own transactions");
        }
    }
}
