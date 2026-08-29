package com.example.transactionstarter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.dto.UpdateTransactionStatusRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 1. Create transaction
    public Transaction createTransaction(CreateTransactionRequest request) {

        validateTransaction(request);

        if (transactionRepository.existsById(request.getTransactionId())) {
            throw new DuplicateTransactionException(
                    "TransactionId already exists: "
                            + request.getTransactionId());
        }

        Transaction transaction = new Transaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());

        // Every new transaction starts as PENDING
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        return transactionRepository.save(transaction);
    }

    // Validation logic
    public void validateTransaction(CreateTransactionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Transaction request must not be null");
        }

        if (request.getTransactionId() == null
                || request.getTransactionId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Transaction ID must not be blank");
        }

        if (request.getCustomerId() == null
                || request.getCustomerId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer ID must not be blank");
        }

        if (request.getAmount() == null
                || request.getAmount().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero");
        }

        if (request.getCurrency() == null) {
            throw new IllegalArgumentException(
                    "Currency must not be null");
        }

        if (request.getTransactionType() == null) {
            throw new IllegalArgumentException(
                    "Transaction type must not be null");
        }
    }

    // 2. Get transaction by ID
    public Transaction getTransactionById(String transactionId) {

        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Transaction ID must not be blank");
        }

        return transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found: "
                                        + transactionId));
    }

    // 3. Update transaction status
    public Transaction updateTransactionStatus(
            String transactionId,
            UpdateTransactionStatusRequest request) {

        if (request == null || request.getTransactionStatus() == null) {
            throw new IllegalArgumentException(
                    "Transaction status must not be null");
        }

        Transaction transaction = getTransactionById(transactionId);

        TransactionStatus currentStatus =
                transaction.getTransactionStatus();

        TransactionStatus newStatus =
                request.getTransactionStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus);
        }

        transaction.setTransactionStatus(newStatus);

        return transactionRepository.save(transaction);
    }

    // Status transition rules
    private boolean isValidStatusTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus) {

        if (currentStatus == TransactionStatus.PENDING) {
            return newStatus == TransactionStatus.PROCESSING
                    || newStatus == TransactionStatus.FAILED;
        }

        if (currentStatus == TransactionStatus.PROCESSING) {
            return newStatus == TransactionStatus.COMPLETED
                    || newStatus == TransactionStatus.FAILED;
        }

        return false;
    }

    // 4. Get all transactions for a customer
    public List<Transaction> getTransactionsByCustomerId(
            String customerId) {

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer ID must not be blank");
        }

        return transactionRepository.findByCustomerId(customerId);
    }
}