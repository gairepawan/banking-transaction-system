package com.portfolio.banking;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        BankingService bank = new BankingService();
        bank.openAccount("ACC-100", "Alex Rivera");
        bank.openAccount("ACC-200", "Jordan Lee");
        bank.deposit("ACC-100", new BigDecimal("1000.00"));
        bank.deposit("ACC-200", new BigDecimal("250.00"));
        bank.transfer("ACC-100", "ACC-200", new BigDecimal("175.50"));
        bank.withdraw("ACC-200", new BigDecimal("25.00"));
        System.out.println("ACCOUNT BALANCES");
        bank.accountsByOwner().forEach(a -> System.out.println(a.id() + " | " + a.owner() + " | $" + a.balance()));
        System.out.println("TOTAL BALANCES: $" + bank.totalBalances());
        System.out.println("TRANSACTIONS FOR ACC-200: " + bank.history("ACC-200").size());
    }
}