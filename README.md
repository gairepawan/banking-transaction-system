# Java Banking Transaction System

A Java 21 educational banking simulation demonstrating OOP, Data Structures and Algorithms (DSA), exact monetary arithmetic, JUnit 5, and GitHub Actions CI.

**Not a real banking application:** This is an in-memory console demo with fictional accounts. It has no authentication, persistent storage, durable transaction ledger, regulatory controls, or real money transfer capabilities.

## Features

- Open accounts with unique account IDs and zero starting balances.
- Deposit and withdraw positive amounts (up to two decimal places).
- Prevent withdrawals and transfers that exceed the source account balance.
- Transfer funds between distinct existing accounts, validating both sides before updating.
- View per-account transaction history, including transfer-in and transfer-out entries.
- Find accounts by account ID and list accounts alphabetically by owner.
- Calculate combined account balances.
- Eight automated JUnit tests and GitHub Actions CI.

## Requirements

- Java Development Kit (JDK) 21
- Apache Maven 3.9+
- Git (optional, for cloning)

Check the tools with `java -version` and `mvn -version`.

## Clone, build, and run

```bash
git clone https://github.com/gairepawan/banking-transaction-system.git
cd banking-transaction-system
mvn clean verify
java -cp target/classes com.portfolio.banking.App
```

Expected example output:

```text
ACCOUNT BALANCES
ACC-100 | Alex Rivera | $824.50
ACC-200 | Jordan Lee | $400.50
TOTAL BALANCES: $1225.00
TRANSACTIONS FOR ACC-200: 3
```

Run just the unit tests using `mvn test`.

## Project layout

```text
banking-transaction-system/
├── .github/workflows/ci.yml
├── .gitignore
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/portfolio/banking/
    │   ├── App.java
    │   ├── Account.java
    │   ├── BankingService.java
    │   └── Transaction.java
    └── test/java/com/portfolio/banking/
        └── BankingServiceTest.java
```

**Account** is an immutable account snapshot, **Transaction** is an immutable ledger-entry snapshot, **BankingService** performs validated operations, and **App** runs the example. The transaction history is in memory only.

## DSA and performance

| Operation | Data structure / algorithm | Typical complexity |
| --- | --- | --- |
| Find account by ID | `HashMap<String, Account>` | O(1) average |
| Record transaction | `ArrayList<Transaction>` | O(1) amortized append |
| Retrieve an account's history | Copy the stored list | O(k), k = history length |
| Sort account owners | Stream sorting with `Comparator` | O(n log n) |
| Total account balances | Linear traversal | O(n) |

Map performance is average-case, not a worst-case guarantee. Exact numerical costs depend on `BigDecimal` operand sizes.

The **transfer conservation invariant** means a valid transfer leaves the combined account balance unchanged. Transfers validate both accounts and sufficient funds before modifying either. The service uses synchronized methods for basic single-process mutual exclusion, but it is not a production transactional database.

## Sample Java usage

```java
BankingService bank = new BankingService();
bank.openAccount("A", "Alex");
bank.openAccount("B", "Jordan");
bank.deposit("A", new BigDecimal("100.00"));
bank.transfer("A", "B", new BigDecimal("25.00"));
System.out.println(bank.findAccount("A").balance()); // 75.00
System.out.println(bank.history("B"));
```

Import `com.portfolio.banking.*` and `java.math.BigDecimal` as needed.

## Automated tests and CI

Eight JUnit 5 tests cover account registration, deposits, withdrawals, balance conservation during transfers, failures that preserve state, validation of amounts, read-only history, sorting and missing accounts.

The workflow at `.github/workflows/ci.yml` runs on pushes to `main` and pull requests targeting `main`. It installs Temurin JDK 21 and runs:

```bash
mvn --batch-mode clean verify
```

See the **Actions** tab to inspect test status. This is **continuous integration**, not production deployment.

## Recommended development workflow

```bash
git switch -c feature/transaction-search
# Implement changes and write tests
mvn clean verify
git add .
git commit -m "feat: add transaction search"
git push -u origin feature/transaction-search
```

Open a pull request, inspect CI results, and merge to `main` after review.

## Planned improvements

1. Interactive command-line menu.
2. Transaction filtering and account statements.
3. Durable SQL database with transactional transfers and migrations.
4. Spring Boot REST API, input validation, authentication and authorization.
5. Web dashboard, audit trails, monitoring, and deployment.

## Security, legal and licensing

Use synthetic names and balances only. Never store banking credentials or real customer details in this demo. The implementation is not suitable for real funds or financial operations. No license has been selected; default copyright protections apply.
