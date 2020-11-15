package com.smhu.account;

public class AccountUpdateWallet {

    private String id;
    private double amount;

    public AccountUpdateWallet() {
    }

    public AccountUpdateWallet(String id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
