# Customer Transaction Service
**Author:** ARIGELA VIZAY SSAI

**Engineering Challenge:** Toucan Payments – Customer Transaction Service

## 1. Problem Understanding

This project is a small transaction-processing service built using Java and Spring Boot.

The purpose of the application is to create and manage customer transactions. Each transaction contains a transaction ID, customer ID, amount, currency, transaction type and transaction status.

I implemented the four operations mentioned in the assignment:

1. Create a transaction
2. Get a transaction by transaction ID
3. Update the status of a transaction
4. Get all transactions belonging to a customer

I focused on keeping the implementation simple and readable rather than adding extra functionality that was not required by the assignment.

---

## 2. Architecture

I used a simple layered structure so that each part of the application has a clear responsibility.

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
H2 Database
```

### Controller

`TransactionController` exposes the REST APIs. It receives HTTP requests and passes the required data to the service layer.

### Service

`TransactionService` contains the main business logic. This is where I perform validation, check for duplicate transactions, retrieve transactions and control transaction status changes.

### Repository

`TransactionRepository` extends `JpaRepository` and is responsible for database operations. It also contains a method to retrieve transactions using a customer ID.

### Entity

`Transaction` is the JPA entity representing a transaction stored in the database.

The transaction ID is used as the primary key.

---

## 3. Assumptions and Validation Rules

The assignment leaves the exact validation rules to the candidate, so I chose simple rules that are reasonable for a transaction service.

### Transaction ID

- Transaction ID is required.
- It must not be blank.
- A transaction ID must be unique.
- If the same transaction ID already exists, the request is rejected.

Example error:

```text
TransactionId already exists: TXN003
```

### Customer ID

- Customer ID is required.
- It must not be blank.

### Amount

- Amount is required.
- The amount must be greater than zero.
- Zero and negative amounts are rejected.

Example error:

```text
Amount must be greater than zero
```

### Currency

The application supports the currencies defined in the `Currency` enum:

- INR
- USD
- EUR

### Transaction Type

The application supports the transaction types defined in the `TransactionType` enum:

- PAYMENT
- REFUND
- TRANSFER

### Initial Status

The client does not provide the initial transaction status while creating a transaction.

Every newly created transaction starts with:

```text
PENDING
```

The status is automatically assigned by the service and cannot be supplied by the client during transaction creation.

---

## 4. Transaction Status Rules

I used a simple status flow to control how a transaction can progress.

The allowed transitions are:

```text
PENDING -> PROCESSING
PENDING -> FAILED
PROCESSING -> COMPLETED
PROCESSING -> FAILED
```

Other status transitions are rejected.

I chose these rules because `COMPLETED` and `FAILED` represent final states, while `PROCESSING` represents a transaction that is currently being processed.

This prevents arbitrary status changes and keeps the transaction lifecycle simple and predictable.

---

## 5. API Endpoints

The application provides the four operations required by the assignment.

### 5.1 Create Transaction

**POST**

```text
/api/transactions
```

Example request:

```json
{
  "transactionId": "TXN1001",
  "customerId": "CUST100",
  "amount": 5000,
  "currency": "INR",
  "transactionType": "PAYMENT"
}
```

The transaction status is automatically set to `PENDING`.

Successful response:

```text
201 Created
```

Example response:

```json
{
  "transactionId": "TXN1001",
  "customerId": "CUST100",
  "amount": 5000.00,
  "currency": "INR",
  "transactionType": "PAYMENT",
  "transactionStatus": "PENDING"
}
```

### 5.2 Get Transaction

**GET**

```text
/api/transactions/{transactionId}
```

Example:

```text
/api/transactions/TXN1001
```

If the transaction exists, its details are returned.

If the transaction does not exist, the API returns:

```text
404 Not Found
```

Example error response:

```json
{
  "status": 404,
  "message": "Transaction not found: TXN999"
}
```

### 5.3 Update Transaction Status

**PUT**

```text
/api/transactions/{transactionId}/status
```

Example:

```text
/api/transactions/TXN1001/status
```

Request body:

```json
{
  "transactionStatus": "PROCESSING"
}
```

The following status transitions are allowed:

```text
PENDING -> PROCESSING
PENDING -> FAILED
PROCESSING -> COMPLETED
PROCESSING -> FAILED
```

Other status transitions are rejected.

For example:

```text
PENDING -> COMPLETED
```

is rejected because the transaction must first move to `PROCESSING`.

An invalid status transition returns:

```text
400 Bad Request
```

Example:

```json
{
  "status": 400,
  "message": "Invalid status transition from PENDING to COMPLETED"
}
```

### 5.4 Get Customer Transactions

**GET**

```text
/api/transactions/customer/{customerId}
```

Example:

```text
/api/transactions/customer/CUST100
```

This returns all transactions belonging to the specified customer.

Example response:

```json
[
  {
    "transactionId": "TXN1001",
    "customerId": "CUST100",
    "amount": 5000.00,
    "currency": "INR",
    "transactionType": "PAYMENT",
    "transactionStatus": "PENDING"
  },
  {
    "transactionId": "TXN1002",
    "customerId": "CUST100",
    "amount": 2500.00,
    "currency": "USD",
    "transactionType": "TRANSFER",
    "transactionStatus": "PENDING"
  }
]
```

---

## 6. Error Handling

I used custom exceptions for business-related errors.

### Duplicate Transaction

If a transaction with the same Transaction ID already exists, the service throws `DuplicateTransactionException`.

The API returns:

```text
409 Conflict
```

Example:

```json
{
  "status": 409,
  "message": "TransactionId already exists: TXN1001"
}
```

### Transaction Not Found

If a requested transaction does not exist, the service throws `TransactionNotFoundException`.

The API returns:

```text
404 Not Found
```

Example:

```json
{
  "status": 404,
  "message": "Transaction not found: TXN999"
}
```

### Invalid Input

Invalid input is handled using `IllegalArgumentException`.

Examples include:

- Blank Transaction ID
- Blank Customer ID
- Amount less than or equal to zero
- Missing Currency
- Missing Transaction Type
- Invalid status transition

These requests return:

```text
400 Bad Request
```

The `GlobalExceptionHandler` uses `@RestControllerAdvice` to convert these exceptions into simple and consistent HTTP responses.

---

## 7. Testing

I added automated tests using JUnit 5 and Spring Boot Test.

The tests currently cover:

1. Successful transaction creation
2. Rejection of an invalid transaction amount
3. Rejection of a duplicate Transaction ID
4. Handling of a transaction that does not exist
5. Successful transaction status update

The starter project also contains the original Spring Boot context test.

The latest test execution completed successfully:

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Before each service test, the existing transactions are deleted using `@BeforeEach`. This keeps the tests independent from each other.

---

## 8. Database

The application uses the H2 embedded in-memory database provided by the starter project.

No separate database installation or database password is required.

The configured database URL is:

```text
jdbc:h2:mem:transactions
```

Spring Data JPA and Hibernate create the required database table from the `Transaction` entity.

The application uses:

```text
spring.jpa.hibernate.ddl-auto=create-drop
```

This means that the database schema is created when the application starts and removed when the application stops.

For this engineering challenge, an in-memory database keeps the application simple and avoids requiring manual database setup.

---

## 9. Project Structure

The project is organised into separate packages based on responsibility.

```text
src/main/java/com/example/transactionstarter

├── controller
│   └── TransactionController.java
│
├── dto
│   ├── CreateTransactionRequest.java
│   └── UpdateTransactionStatusRequest.java
│
├── entity
│   └── Transaction.java
│
├── enums
│   ├── Currency.java
│   ├── TransactionStatus.java
│   └── TransactionType.java
│
├── exception
│   ├── DuplicateTransactionException.java
│   ├── TransactionNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── repository
│   └── TransactionRepository.java
│
├── service
│   └── TransactionService.java
│
└── TransactionStarterApplication.java
```

The controller handles HTTP requests, the service contains the business logic, and the repository handles database operations.

The DTOs are separate from the entity so that the API request does not directly control fields such as the initial transaction status.

---

## 10. How to Run

### Windows

Run the tests:

```text
mvnw.cmd clean test
```

Start the application:

```text
mvnw.cmd spring-boot:run
```

### Linux / macOS

Run the tests:

```text
./mvnw clean test
```

Start the application:

```text
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

The REST APIs can be tested using Postman.

---

## 11. Known Limitations

This implementation is focused on the requirements of the engineering challenge and is not intended to be a complete production payment system.

Some current limitations are:

- H2 is an in-memory database, so data is not persisted after the application stops.
- Authentication and authorization are not implemented because they were not required by the assignment.
- Customer transactions are returned without pagination.
- There is no external payment processing integration.
- Error responses are intentionally simple.
- Production-level monitoring and auditing are not implemented.

---

## 12. What I Would Improve With More Time

If this application were developed further for a production environment, I would consider:

- Adding more detailed Jakarta Bean Validation.
- Adding controller-level integration tests for all four APIs.
- Adding pagination for customer transactions.
- Improving the error response structure.
- Adding logging and request tracing.
- Replacing H2 with a production database.
- Adding authentication and authorization.
- Adding API documentation using OpenAPI/Swagger.
- Adding timestamps and audit information for transactions.

I intentionally kept the implementation focused on the requirements of this engineering challenge rather than adding unnecessary functionality.

---

## 13. Final Verification

I verified the application using:

```text
mvnw.cmd clean test
```

The latest test execution completed successfully:

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

I also verified the REST operations using Postman.

The four required operations were tested:

- Create transaction
- Get transaction
- Update transaction status
- Get customer transactions

The project is ready for the final submission checks after confirming the assigned candidate variant and preparing the AI Usage Disclosure.

## Author

**ARIGELA VIZAY SSAI**

This project was completed as part of the Toucan Payments engineering challenge.