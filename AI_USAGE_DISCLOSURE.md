# AI Usage Disclosure

I used ChatGPT as a supporting development and review tool while working on
this assignment.

I have prior knowledge of Java, object-oriented programming, REST APIs and
Spring Boot, and I used that knowledge while designing and implementing the
solution. AI assistance was mainly used to clarify requirements, review
implementation choices and troubleshoot specific issues during development.

## How I Used AI

I used AI assistance for:

- Understanding and breaking down the assignment requirements.
- Discussing possible approaches for structuring the transaction service.
- Reviewing Java and Spring Boot implementation choices.
- Reviewing validation and exception-handling approaches.
- Reviewing and improving automated tests.
- Reviewing the README and documentation.
- Troubleshooting specific issues encountered during development and API
  testing with Postman.

## What AI Suggested

AI suggested implementation approaches for parts of the controller, service,
exception handling, validation logic, status transition logic and tests.

It also suggested documentation structure and example API requests.

These suggestions were reviewed against the actual starter project and the
assignment requirements before being used.

## What I Changed and Verified

I made the implementation decisions based on the requirements and adapted the
suggestions to the existing starter project.

I reviewed the generated or suggested code rather than using it without
understanding it. I checked the implementation by running the application,
executing the automated tests and testing the REST APIs using Postman.

The REST APIs were tested for:

- Successful transaction creation.
- Retrieving a transaction by transaction ID.
- Updating transaction status.
- Retrieving transactions for a customer.
- Rejecting duplicate transaction IDs.
- Rejecting invalid transaction amounts.
- Handling transactions that do not exist.
- Rejecting invalid status transitions.

## Corrections During Development

Some issues encountered during development were related to API testing, such
as using an incorrect HTTP method or attempting to update a transaction that
was not present in the current H2 in-memory database.

I corrected these issues and verified the expected behaviour through Postman.

I also checked that the status transition implementation matched the rules
documented in the README.

## Verification of the Final Solution

I ran the complete Maven test suite using:

    mvnw.cmd clean test

The final test run completed successfully with:

    Tests run: 7
    Failures: 0
    Errors: 0
    Skipped: 0

The application was also started locally and all four required REST operations
were tested using Postman.

Before submission, I reviewed the implementation to ensure that I can explain
the classes, business logic, validation rules, status transitions, exception
handling, API design and automated tests.