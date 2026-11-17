package com.portfolio.banking;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class BankingServiceTest {
    @Test void accountStartsAtZeroAndRejectsDuplicates() {
        BankingService bank = new BankingService();
        assertEquals(new BigDecimal("0.00"), bank.openAccount("A", "Alex").balance());
        assertThrows(IllegalArgumentException.class, () -> bank.openAccount("A", "Duplicate"));
    }
    @Test void depositsAndWithdrawalsChangeBalance() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex");
        bank.deposit("A", new BigDecimal("100.00")); bank.withdraw("A", new BigDecimal("40.25"));
        assertEquals(new BigDecimal("59.75"), bank.findAccount("A").balance());
        assertEquals(2, bank.history("A").size());
    }
    @Test void transferConservesMoney() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex"); bank.openAccount("B", "Beth");
        bank.deposit("A", new BigDecimal("100.00"));
        bank.transfer("A", "B", new BigDecimal("30.25"));
        assertEquals(new BigDecimal("69.75"), bank.findAccount("A").balance());
        assertEquals(new BigDecimal("30.25"), bank.findAccount("B").balance());
        assertEquals(new BigDecimal("100.00"), bank.totalBalances());
        assertEquals(Transaction.Type.TRANSFER_OUT, bank.history("A").get(1).type());
        assertEquals(Transaction.Type.TRANSFER_IN, bank.history("B").get(0).type());
    }
    @Test void insufficientFundsLeavesAllBalancesAndHistoryUnchanged() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex"); bank.openAccount("B", "Beth");
        bank.deposit("A", new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> bank.transfer("A", "B", new BigDecimal("15.00")));
        assertEquals(new BigDecimal("10.00"), bank.findAccount("A").balance());
        assertEquals(new BigDecimal("0.00"), bank.findAccount("B").balance());
        assertEquals(1, bank.history("A").size()); assertTrue(bank.history("B").isEmpty());
    }
    @Test void missingDestinationLeavesSourceUnchanged() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex"); bank.deposit("A", new BigDecimal("10.00"));
        assertThrows(NoSuchElementException.class, () -> bank.transfer("A", "MISSING", new BigDecimal("2.00")));
        assertEquals(new BigDecimal("10.00"), bank.findAccount("A").balance());
    }
    @Test void invalidAmountsAreRejected() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex");
        assertThrows(IllegalArgumentException.class, () -> bank.deposit("A", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> bank.deposit("A", new BigDecimal("1.001")));
        assertThrows(IllegalArgumentException.class, () -> bank.withdraw("A", new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> bank.transfer("A", "A", BigDecimal.ONE));
    }
    @Test void historyIsReadOnlyAndPerAccount() {
        BankingService bank = new BankingService(); bank.openAccount("A", "Alex");
        bank.deposit("A", new BigDecimal("2.00"));
        assertThrows(UnsupportedOperationException.class, () -> bank.history("A").clear());
        assertEquals(1, bank.history("A").size());
    }
    @Test void sortsAccountsByOwnerAndChecksMissing() {
        BankingService bank = new BankingService(); bank.openAccount("B", "Zoe"); bank.openAccount("A", "Amy");
        assertEquals("Amy", bank.accountsByOwner().getFirst().owner());
        assertThrows(NoSuchElementException.class, () -> bank.findAccount("X"));
    }
}