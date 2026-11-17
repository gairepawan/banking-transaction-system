package com.portfolio.banking;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/** An immutable, in-memory ledger entry (not a durable financial audit log). */
public record Transaction(String id, String accountId, Type type, BigDecimal amount,
                          BigDecimal balanceAfter, String relatedAccountId, Instant timestamp) {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }

    public Transaction {
        Objects.requireNonNull(id);
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(balanceAfter);
        Objects.requireNonNull(timestamp);
    }
}