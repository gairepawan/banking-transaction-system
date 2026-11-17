package com.portfolio.banking;

import java.math.BigDecimal;
import java.util.Objects;

/** Immutable account snapshot; balance can only be changed by BankingService. */
public record Account(String id, String owner, BigDecimal balance) {
    public Account {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Account ID is required");
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("Owner is required");
        Objects.requireNonNull(balance, "Balance is required");
        if (balance.signum() < 0 || balance.scale() > 2)
            throw new IllegalArgumentException("Balance must be nonnegative with at most two decimal places");
        id = id.trim();
        owner = owner.trim();
        balance = balance.setScale(2);
    }
}