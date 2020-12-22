package com.smhu.account;

public class AccountUpdateWallet {

    private String username;
    private double amount;

    public AccountUpdateWallet() {
    }

    public AccountUpdateWallet(String username, double amount) {
        this.username = username;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
