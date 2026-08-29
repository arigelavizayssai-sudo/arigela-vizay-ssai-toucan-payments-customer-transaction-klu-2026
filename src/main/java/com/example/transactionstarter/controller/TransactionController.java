package com.example.transactionstarter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.dto.UpdateTransactionStatusRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. Create transaction
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @RequestBody CreateTransactionRequest request) {

        return transactionService.createTransaction(request);
    }

    // 2. Get transaction by transaction ID
    @GetMapping("/{transactionId}")
    public Transaction getTransactionById(
            @PathVariable String transactionId) {

        return transactionService.getTransactionById(transactionId);
    }

    // 3. Update transaction status
    @PutMapping("/{transactionId}/status")
    public Transaction updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestBody UpdateTransactionStatusRequest request) {

        return transactionService.updateTransactionStatus(
                transactionId, request);
    }

    // 4. Get all transactions for a customer
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getTransactionsByCustomerId(
            @PathVariable String customerId) {

        return transactionService.getTransactionsByCustomerId(customerId);
    }
}