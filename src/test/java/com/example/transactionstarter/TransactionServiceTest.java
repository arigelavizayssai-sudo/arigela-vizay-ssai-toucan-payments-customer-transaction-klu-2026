package com.example.transactionstarter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.dto.UpdateTransactionStatusRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.enums.Currency;
import com.example.transactionstarter.enums.TransactionStatus;
import com.example.transactionstarter.enums.TransactionType;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import com.example.transactionstarter.service.TransactionService;

@SpringBootTest
public class TransactionServiceTest {
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private TransactionRepository transactionRepository;
	@BeforeEach	
	void setUp() {
		transactionRepository.deleteAll();
	}	

	//TEST 1:transaction created successfully
	@Test
	void shouldCreateTransactionSuccessfully() {
		CreateTransactionRequest request = createValidRequest("TXN001");
		Transaction transaction =
				transactionService.createTransaction(request);
		assertNotNull(transaction);
		assertEquals(
				"TXN001",
				transaction.getTransactionId()
				);
		assertEquals("CUST001",transaction.getCustomerId());
		assertEquals(new BigDecimal("5000"),transaction.getAmount());
		assertEquals(TransactionStatus.PENDING,transaction.getTransactionStatus());


	}
	//test2 invalid transaction rejected
	@Test
	void shouldRejectInvalidTransaction() {
		CreateTransactionRequest request = createValidRequest("TXN002");

		request.setAmount(new BigDecimal("-100"));
		IllegalArgumentException exception = 
				assertThrows(IllegalArgumentException.class,
						()->transactionService.createTransaction(request));
		assertEquals("Amount must be greater than zero"
				,exception.getMessage());
	}
	@Test
	//duplicate transaction id rejected
	void shouldRejectDuplicateTransactionId() {
		CreateTransactionRequest request = createValidRequest("TXN003");
		transactionService.createTransaction(request);
		DuplicateTransactionException exception =
				assertThrows(DuplicateTransactionException.class,
						()->transactionService.createTransaction(request));
		assertEquals("TransactionId already exists: TXN003",
				exception.getMessage());
	}
	//4 transaction not found
	@Test
	void shouldThrowExceptionWhenTransactionNotFound() {
		TransactionNotFoundException exception = assertThrows(
				TransactionNotFoundException.class,()->
				transactionService.getTransactionById("TXN999")
				);
		assertEquals(
				"Transaction not found: TXN999",
				exception.getMessage()
				);
	}
	//5 valid transaction status update
	@Test
	void shouldUpdateTransactionStatusSuccessfully() {
		CreateTransactionRequest request = createValidRequest("TXN004"); 
		transactionService.createTransaction(request);
		UpdateTransactionStatusRequest updateRequest =
				new UpdateTransactionStatusRequest();
		updateRequest.setTransactionStatus(
				TransactionStatus.PROCESSING);
		Transaction updatedTransaction =
				transactionService.updateTransactionStatus(
						"TXN004",
						updateRequest);

		assertEquals(
				TransactionStatus.PROCESSING,
				updatedTransaction.getTransactionStatus());
	}

	//helper method to help to create valid test data
	private CreateTransactionRequest createValidRequest(String transactionId) {
		CreateTransactionRequest request = new CreateTransactionRequest();
		request.setTransactionId(transactionId);
		request.setCustomerId("CUST001");
		request.setAmount(new BigDecimal("5000"));
		request.setCurrency(Currency.INR);
		request.setTransactionType(TransactionType.PAYMENT);
		return request;

	}

	//6 invalid transaction status update
	@Test
	void shouldRejectInvalidTransactionStatusTransition() {
		CreateTransactionRequest request = createValidRequest("TXN005");
		transactionService.createTransaction(request);
		UpdateTransactionStatusRequest updateRequest = 
				new UpdateTransactionStatusRequest();

		updateRequest.setTransactionStatus(TransactionStatus.COMPLETED);
		IllegalArgumentException exception =	
				assertThrows(IllegalArgumentException.class,
						()->transactionService.
						updateTransactionStatus("TXN005", updateRequest));

		assertEquals(
				"Invalid status transition from PENDING to COMPLETED",
				exception.getMessage());
	}
}
