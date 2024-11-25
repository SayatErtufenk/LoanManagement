# Loan API Project

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [Database Initialization](#database-initialization)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
    - [Authentication](#authentication)
    - [Customer Management](#customer-management)
    - [Loan Management](#loan-management)
    - [Payment Management](#payment-management)
- [Testing the API](#testing-the-api)
    - [Using Postman](#using-postman)
- [Security and Authorization](#security-and-authorization)
- [Error Handling](#error-handling)
- [Important Notes](#important-notes)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

This project is a **Loan API** developed for a bank, enabling their employees to create, list, and pay loans for customers. It provides RESTful endpoints secured with JWT-based authentication and role-based authorization.

## Features

- **Create Loan**: Bank employees can create loans for customers with specific amounts, interest rates, and installments.
- **List Loans**: Ability to list loans for customers with optional filters.
- **List Installments**: View installments for a specific loan.
- **Pay Loan**: Make payments towards loan installments with automatic handling of reward and penalty calculations.
- **Role-based Access Control**:
    - **ADMIN** users can perform operations for all customers.
    - **CUSTOMER** users can perform operations for themselves.
- **Security**: JWT-based authentication with encrypted passwords.
- **Validation**: Ensures business rules are followed, such as installment counts, interest rates, and credit limits.

## Technologies Used

- **Java 11** or higher
- **Spring Boot 2.7** or higher
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **Spring Data JPA**
- **H2 In-Memory Database**
- **Maven** for build and dependency management
- **Lombok** for reducing boilerplate code

## Prerequisites

- **Java 11** or higher installed on your system
- **Maven** installed for building the project
- An IDE like **IntelliJ IDEA** or **Eclipse** (optional but recommended)

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/SayatErtufenk/LoanManagement.git
cd LoanManagement
```

### 2. Build the Project

```bash
mvn clean install
```
               
### 3. Application Properties
The application uses default configurations suitable for local development. You can modify src/main/resources/application.properties if needed.

## Database Initialization
Using data.sql
The application initializes some default data using the data.sql file located in src/main/resources. This includes an admin user and a customer user.

```sql
INSERT INTO customer (name, surname, username, password, role, credit_limit, used_credit_limit) VALUES
('Admin', 'User', 'admin', '$2a$10$ozAkVsCuSMR0ouY6rLdjPu6jzls7iGbqsn3T4fRwVPFJdszTq9YOa', 'ADMIN', 100000, 0);

INSERT INTO customer (name, surname, username, password, role, credit_limit, used_credit_limit) VALUES
('Default', 'Customer', 'customer', '$2a$10$dXJvZcnuK2bFvWcfwMJJMOr5uooKx5FtZAsEA0y0LOxwkPbK96ZCm', 'CUSTOMER', 50000, 0);
```

**Note:** The passwords are hashed versions of **admin** and **customerpass** respectively.

## Running the Application

You can run the application using Maven:

```bash
mvn spring-boot:run
```

Or by running the generated JAR file:

```bash
java -jar target/loan-api-1.0.0.jar
```

The application will start on http://localhost:8080.

## API Endpoints
### Authentication
#### Obtain JWT Token

**Endpoint:** POST /authenticate

**Description:** Authenticate user and receive a JWT token.

**Request**
```json
{
    "username": "admin",
    "password": "admin"
}
```
Replace the username and password with valid credentials.

**Response**
```json
{"jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczMjUyNTg5MywiZXhwIjoxNzMyNTYxODkzfQ.GZc2cxfhe0VhnL" }
``` 

## Customer Management
### Create Customer
**Endpoint:** POST /customers

**Description:** Create a new customer.

**Authorization:** Requires ADMIN role.

**Request Headers**

- Authorization: Bearer <JWT_TOKEN>

- Content-Type: application/json

**Request Body**
```json
{
    "name": "Sayat",
    "surname": "Ertufenk",
    "username": "sayatertufenk",
    "password": "sayat123",
    "role": "CUSTOMER",
    "creditLimit": 50000
}
```
**Response**
```json
{
    "id": 3,
    "name": "Sayat",
    "surname": "Ertufenk",
    "username": "sayatertufenk",
    "creditLimit": 50000,
    "usedCreditLimit": 0,
    "role": "CUSTOMER"
} 
```

### Notes: 

- The password will be hashed before storing.

- The username must be unique.

### 2. List All Customers 

**Endpoint:** GET /customers
              
**Description:** Retrieve a list of all customers.

**Authorization:** Requires ADMIN role.

## Loan Management
### 1. Create Loan
**Endpoint:** POST /loans

**Description:** Create a new loan for a customer.

**Authorization:** Requires ADMIN role.

**Request Headers**

- Authorization: Bearer <JWT_TOKEN>

- Content-Type: application/json

**Request Body**
```json
{
    "customerId": 3,
    "amount": 10000,
    "interestRate": 0.15,
    "installmentCount": 12
}
```
**Response**
```json
{
  "loanId": 1,
  "totalAmount": 23000.00,
  "installmentAmount": 1916.67,
  "numberOfInstallments": 12,
  "isPaid": false
}
```

### Notes:

- The interestRate must be between 0.1 and 0.5.

- The numberOfInstallment can only be 6, 9, 12, or 24.

- The customer's credit limit is checked before creating the loan.

### 2. List All Loans 
**Endpoint:** GET /loans/all

**Description:** Retrieve a list of all loans.

**Authorization:** Requires ADMIN role.

### 3. List Customer's Loans 
**Endpoint:** GET /loans/my-loans

**Description:** Retrieve loans associated with the authenticated customer.

**Authorization:** Requires CUSTOMER role.

## Payment Management
### 1. Pay Loan Installments
**Endpoint:** POST /payments/loan/{loanId}?amount={amount}

**Description:** Make a payment towards loan installments.

**Authorization:** Requires CUSTOMER role for own loans, ADMIN role for any loan.

**Parameters:**

- loanId: ID of the loan to make payment on.

- amount: Total amount to be paid.

**Request Headers**

- Authorization: Bearer <JWT_TOKEN>

**Response**
```json
{
  "numberOfInstallmentsPaid": 2,
  "totalAmountPaid": 3800.00,
  "isLoanFullyPaid": false,
  "totalDiscount": 40.00,
  "totalPenalty": 0.00
}
```
### Notes

- Installments must be paid in full, no partial payments.

- Earliest installments are paid first.

- Installments due more than 3 months in the future cannot be paid.

- Reward and penalty calculations are applied based on payment timing.

## Testing the API

### Using Postman
- Import the collection or create requests manually. (Loan Management.postman_collection.json)

- Set up environment variables for BASE_URL and JWT_TOKEN.

- Use the Authorization tab in Postman and select Bearer Token.

- Paste the JWT token obtained from the authentication step.

- Make requests to the API endpoints.

## Security and Authorization
- **JWT Authentication:** All endpoints except /authenticate and /h2-console/** require a valid JWT token.

- **Roles:**

- - **ADMIN:**

- - - Can perform all operations.

- - - Access to all customers and loans.

- - **CUSTOMER:**

- - - Can perform operations only for themselves.

- - - Can view and pay their own loans.

- **Password Encryption:** Passwords are stored using BCrypt hashing.

- **Role-Based Method Security:** Annotations like @PreAuthorize are used to enforce security at the method level.

## Error Handling
The application uses a global exception handler to return meaningful error messages.

- DuplicateUsernameException: Thrown when trying to create a customer with an existing username.

- ResourceNotFoundException: Thrown when a requested resource is not found.

- InvalidParameterException: Thrown when invalid parameters are provided.

- InsufficientCreditLimitException: Thrown when the customer's credit limit is insufficient.

- Sample Error Response:
```json
{
  "timestamp": "2023-05-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "This username is already taken.",
  "path": "/customers"
}
```

## Important Notes
- **Interest Rates and Installments:**

- - Interest rate must be between 0.1 and 0.5.

- - Installment counts can only be 6, 9, 12, or 24.

- **Installment Calculations:**

- - All installments have the same amount.

- - Total loan amount is calculated as amount * (1 + interestRate).

- - Installment due dates are the first day of each month, starting from the next month.

- **Reward and Penalty Mechanism:**

- - Early payments receive a discount: 0.001 * days before due date * installment amount.

- - Late payments incur a penalty: 0.001 * days after due date * installment amount.

- **Data Integrity:**

- - The username field in Customer is unique.

- - Validation is enforced at both application and database levels.

## Future Improvements
- **Swagger Documentation:** Integrate Swagger for API documentation and testing.

- **Email Notifications:** Implement email notifications for due installments.

- **Frontend Application:** Develop a frontend interface for end-users.

- **Dockerization:** Containerize the application using Docker.

- **Database Migration Tool:** Use Flyway or Liquibase for database versioning.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

## License
This project is licensed under the MIT License.