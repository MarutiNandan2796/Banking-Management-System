# Banking Management System

![Java](https://img.shields.io/badge/Java-11-blue)
![Maven](https://img.shields.io/badge/Maven-3.8+-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Version](https://img.shields.io/badge/Version-1.1-brightgreen)

A comprehensive **Banking Management System** built with **Core Java**, **JDBC**, **Maven**, and **MySQL** that demonstrates professional software development practices including CRUD operations, OOP principles, exception handling, logging, password security, and DevOps integration.

## 🚀 Version History
- **v1.1** - Enhanced documentation and performance improvements
- **v1.0** - Initial release with core banking features

---

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Git Commands](#git-commands)
- [Interview Talking Points](#interview-talking-points)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### Core Banking Features
- **User Authentication**
  - Secure user registration with password hashing (BCrypt)
  - Login with username/password validation
  - Password strength validation
  - Change password functionality

- **Account Management**
  - Open multiple account types (Savings, Current, Fixed Deposit, Recurring Deposit)
  - View all accounts
  - View detailed account information
  - Close accounts (with balance validation)
  - Account status management (Active, Inactive, Suspended, Closed)

- **Banking Operations**
  - Deposit money
  - Withdraw money (with balance validation)
  - Transfer funds between accounts
  - Check account balance
  - Real-time balance updates

- **Transaction Management**
  - Complete transaction history
  - Transaction types: Deposit, Withdrawal, Transfer In/Out
  - Transaction date tracking
  - Balance after each transaction

### Technical Features
- **3-Tier Architecture** (Model-DAO-Service)
- **Exception Handling** throughout the application
- **Input Validation** (email, phone, username, password)
- **Logging** using SLF4J
- **Password Security** using BCrypt hashing
- **Database Connection Management** using Singleton pattern
- **JDBC** for database operations
- **JUnit 5** test cases
- **Maven** for dependency management and build automation

---

## 🛠 Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11+ | Core programming language |
| Maven | 3.8+ | Build automation & dependency management |
| MySQL | 8.0+ | Relational database |
| JDBC | 8.0.33 | Database connectivity |
| SLF4J | 2.0.7 | Logging framework |
| JUnit | 5.9.3 | Unit testing |
| BCrypt | 0.4 | Password hashing |
| Git | Latest | Version control |

---

## 📁 Project Structure

```
BankingManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/com/bms/
│   │   │   ├── model/              # Entity classes
│   │   │   │   ├── Account.java
│   │   │   │   ├── Customer.java
│   │   │   │   └── Transaction.java
│   │   │   ├── dao/                # Data Access Objects
│   │   │   │   ├── AccountDAO.java
│   │   │   │   ├── CustomerDAO.java
│   │   │   │   └── TransactionDAO.java
│   │   │   ├── service/            # Business Logic Layer
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── AccountService.java
│   │   │   │   └── TransactionService.java
│   │   │   ├── util/               # Utility Classes
│   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── PasswordUtil.java
│   │   │   │   ├── ValidationUtil.java
│   │   │   │   └── AccountNumberGenerator.java
│   │   │   └── Main.java           # Application Entry Point
│   │   └── resources/
│   │       ├── db.properties       # Database configuration
│   │       └── simplelogger.properties  # Logging configuration
│   └── test/
│       └── java/com/bms/
│           ├── service/            # Service tests
│           └── util/               # Utility tests
├── bank.sql                        # Database setup script
├── pom.xml                         # Maven configuration
├── .gitignore                      # Git ignore patterns
└── README.md                       # Project documentation
```

---

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   ```

2. **Apache Maven 3.8+**
   ```bash
   mvn -version
   ```

3. **MySQL 8.0+**
   ```bash
   mysql --version
   ```

4. **Git** (for version control)
   ```bash
   git --version
   ```

5. **IDE** (IntelliJ IDEA or Eclipse recommended)

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/BankingManagementSystem.git
cd BankingManagementSystem
```

### Step 2: Setup MySQL Database

1. Start MySQL server
2. Open MySQL command line or MySQL Workbench
3. Run the database setup script:

```bash
mysql -u root -p < bank.sql
```

Or execute the SQL file in MySQL Workbench.

This will:
- Create `banking_db` database
- Create tables: `customers`, `accounts`, `transactions`
- Insert sample data
- Create views for reporting

### Step 3: Configure Database Connection

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/banking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

**Important**: Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL password.

### Step 4: Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests
- Package the application

---

## ▶️ Running the Application

### Method 1: Using Maven (Recommended)

```bash
mvn exec:java -Dexec.mainClass="com.bms.Main"
```

### Method 2: Using JAR file

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/banking-management-system-1.0.0.jar
```

### Method 3: Using IDE

1. Open the project in IntelliJ IDEA or Eclipse
2. Navigate to `src/main/java/com/bms/Main.java`
3. Right-click and select "Run Main.main()"

---

## 📖 Usage Guide

### Sample Credentials

The database comes with pre-configured test accounts:

**Admin Account:**
- Username: `admin`
- Password: `Admin@123`

**Test User:**
- Username: `johndoe`
- Password: `Test@123`
- Pre-existing accounts: ACC123456789 (Savings), ACC987654321 (Current)

### Application Flow

1. **Start the Application**
   - Choose Login or Register

2. **Registration** (New Users)
   - Provide personal details
   - Create username and strong password
   - Receive Customer ID

3. **Login**
   - Enter username and password
   - Access main menu

4. **Account Management**
   - Open new accounts
   - View all accounts
   - Check account details
   - Close accounts

5. **Banking Operations**
   - Deposit money
   - Withdraw money
   - Transfer funds
   - Check balance

6. **Transaction History**
   - View complete transaction history
   - Track all deposits, withdrawals, and transfers

7. **Profile Management**
   - View profile information
   - Change password

### Password Requirements

Passwords must contain:
- At least 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character

---

## 🗄️ Database Schema

### Tables

**customers**
- `customer_id` (BIGINT, Primary Key, Auto Increment)
- `first_name`, `last_name`, `email`, `phone`, `address`
- `username` (UNIQUE), `password_hash`
- `created_at`, `updated_at` (TIMESTAMP)

**accounts**
- `account_id` (BIGINT, Primary Key, Auto Increment)
- `customer_id` (Foreign Key → customers)
- `account_number` (UNIQUE)
- `account_type` (ENUM: SAVINGS, CURRENT, FIXED_DEPOSIT, RECURRING_DEPOSIT)
- `balance` (DECIMAL)
- `status` (ENUM: ACTIVE, INACTIVE, SUSPENDED, CLOSED)
- `created_at`, `updated_at` (TIMESTAMP)

**transactions**
- `transaction_id` (BIGINT, Primary Key, Auto Increment)
- `account_id` (Foreign Key → accounts)
- `transaction_type` (ENUM: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)
- `amount`, `balance_after` (DECIMAL)
- `description`, `related_account_id`
- `transaction_date` (TIMESTAMP)

### Views

- `customer_account_summary` - Customer account statistics
- `account_transaction_summary` - Account transaction statistics

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=PasswordUtilTest
```

### Test Coverage

The project includes unit tests for:
- Password utility functions
- Input validation
- Service layer methods (sample tests)

---

## 📝 Git Commands

### Initial Setup

```bash
# Initialize Git repository
git init

# Add all files
git add .

# First commit
git commit -m "Initial commit: Banking Management System"

# Add remote repository
git remote add origin https://github.com/yourusername/BankingManagementSystem.git

# Push to GitHub
git push -u origin main
```

### Regular Workflow

```bash
# Check status
git status

# Add changes
git add .

# Commit changes
git commit -m "Add new feature: transaction history"

# Push to remote
git push origin main

# Pull latest changes
git pull origin main

# Create new branch
git checkout -b feature/new-feature

# Merge branch
git checkout main
git merge feature/new-feature
```

---

## 💼 Interview Talking Points

When presenting this project in interviews, highlight:

### Technical Skills
1. **Object-Oriented Programming**
   - Proper encapsulation with getters/setters
   - Inheritance and polymorphism
   - Enum types for type safety

2. **Design Patterns**
   - **Singleton Pattern** - DatabaseConnection
   - **DAO Pattern** - Data access layer separation
   - **Service Layer Pattern** - Business logic separation

3. **SOLID Principles**
   - Single Responsibility Principle
   - Dependency Inversion
   - Open/Closed Principle

4. **Database Management**
   - JDBC connectivity
   - Prepared statements (SQL injection prevention)
   - Transaction management
   - Foreign key relationships

5. **Security**
   - BCrypt password hashing
   - Input validation
   - SQL injection prevention

6. **Best Practices**
   - Exception handling
   - Logging (SLF4J)
   - Code documentation
   - Modular architecture

### Problem-Solving Skills
- **Concurrency**: How would you handle multiple simultaneous transactions?
- **Scalability**: How would you scale this to handle millions of users?
- **Performance**: How would you optimize database queries?

### DevOps Integration
- Maven for build automation
- JUnit for testing
- Git for version control
- Properties files for configuration management

---

## 🔮 Future Enhancements

Potential improvements to discuss:

1. **Technical**
   - RESTful API using Spring Boot
   - Microservices architecture
   - Connection pooling (HikariCP)
   - Redis caching
   - JWT authentication

2. **Features**
   - Account statements (PDF generation)
   - Email notifications
   - SMS alerts
   - Interest calculation
   - Loan management
   - Credit card management

3. **DevOps**
   - Docker containerization
   - CI/CD pipeline (Jenkins/GitHub Actions)
   - Cloud deployment (AWS/Azure)
   - Monitoring (Prometheus, Grafana)

4. **Frontend**
   - Web interface (React/Angular)
   - Mobile app (React Native/Flutter)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Oracle Java Documentation
- MySQL Documentation
- Maven Central Repository
- JUnit Documentation
- Open Source Community

---

## 📞 Support

For support, email your.email@example.com or open an issue in the GitHub repository.

---

**⭐ If you find this project useful, please consider giving it a star on GitHub!**
