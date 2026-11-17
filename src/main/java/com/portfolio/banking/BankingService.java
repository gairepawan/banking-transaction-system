package com.portfolio.banking;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** Educational in-memory banking ledger. Not suitable for handling real funds. */
public final class BankingService {
    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, List<Transaction>> history = new HashMap<>();
    private long sequence = 1;

    public synchronized Account openAccount(String id, String owner) {
        Account account = new Account(id, owner, BigDecimal.ZERO);
        if (accounts.containsKey(account.id())) throw new IllegalArgumentException("Duplicate account ID");
        accounts.put(account.id(), account);
        history.put(account.id(), new ArrayList<>());
        return account;
    }

    public synchronized Account findAccount(String id) {
        Account account = accounts.get(id);
        if (account == null) throw new NoSuchElementException("Unknown account: " + id);
        return account;
    }

    public synchronized Account deposit(String id, BigDecimal amount) {
        BigDecimal valid = requireAmount(amount);
        Account current = findAccount(id);
        Account updated = new Account(id, current.owner(), current.balance().add(valid));
        accounts.put(id, updated);
        log(id, Transaction.Type.DEPOSIT, valid, updated.balance(), null);
        return updated;
    }

    public synchronized Account withdraw(String id, BigDecimal amount) {
        BigDecimal valid = requireAmount(amount);
        Account current = findAccount(id);
        ensureSufficient(current, valid);
        Account updated = new Account(id, current.owner(), current.balance().subtract(valid));
        accounts.put(id, updated);
        log(id, Transaction.Type.WITHDRAWAL, valid, updated.balance(), null);
        return updated;
    }

    /** Validates both sides before modifying either balance. */
    public synchronized void transfer(String fromId, String toId, BigDecimal amount) {
        BigDecimal valid = requireAmount(amount);
        if (fromId == null || fromId.equals(toId))
            throw new IllegalArgumentException("Source and destination must be different");
        Account from = findAccount(fromId);
        Account to = findAccount(toId);
        ensureSufficient(from, valid);
        Account updatedFrom = new Account(fromId, from.owner(), from.balance().subtract(valid));
        Account updatedTo = new Account(toId, to.owner(), to.balance().add(valid));
        accounts.put(fromId, updatedFrom);
        accounts.put(toId, updatedTo);
        log(fromId, Transaction.Type.TRANSFER_OUT, valid, updatedFrom.balance(), toId);
        log(toId, Transaction.Type.TRANSFER_IN, valid, updatedTo.balance(), fromId);
    }

    public synchronized List<Transaction> history(String id) {
        findAccount(id);
        return List.copyOf(history.get(id));
    }

    public synchronized List<Account> accountsByOwner() {
        return accounts.values().stream().sorted(
            Comparator.comparing(Account::owner, String.CASE_INSENSITIVE_ORDER).thenComparing(Account::id)
        ).toList();
    }

    public synchronized BigDecimal totalBalances() {
        return accounts.values().stream().map(Account::balance).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    private void log(String id, Transaction.Type type, BigDecimal amount, BigDecimal balance, String related) {
        String transactionId = "TXN" + String.format("%07d", sequence++);
        history.get(id).add(new Transaction(transactionId, id, type, amount, balance, related, Instant.now()));
    }

    private static BigDecimal requireAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new IllegalArgumentException("Amount must be positive with at most 2 decimal places");
        return amount.setScale(2);
    }

    private static void ensureSufficient(Account account, BigDecimal amount) {
        if (account.balance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds in " + account.id());
    }
}