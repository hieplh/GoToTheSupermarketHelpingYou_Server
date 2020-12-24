package com.smhu.account;

public class AccountUpdate {

    private String username;
    private String oldPwd;
    private String newPwd;
    private double amount;
    private String role;

    public AccountUpdate() {
    }

    public AccountUpdate(String username, String oldPwd, String newPwd, double amount, String role) {
        this.username = username;
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
        this.amount = amount;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AccountUpdate{" + "username=" + username + ", oldPwd=" + oldPwd + ", newPwd=" + newPwd + ", amount=" + amount + ", role=" + role + '}';
    }

}
