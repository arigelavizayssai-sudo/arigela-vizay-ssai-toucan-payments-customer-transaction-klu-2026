package com.example.transactionstarter.dto;

import com.example.transactionstarter.enums.TransactionStatus;

public class UpdateTransactionStatusRequest {

    private TransactionStatus transactionStatus;

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}