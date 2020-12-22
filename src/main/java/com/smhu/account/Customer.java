package com.smhu.account;

import java.util.List;

public class Customer extends Account {

    private int numSuccess;
    private int numCancel;
    private double wallet;
    private List<Address> addresses;

    public Customer() {
    }

    public Customer(Account account, int numSuccess, int numCancel, double wallet, List<Address> addresses) {
        super(account.getUsername(), account.getFirstName(), account.getMiddleName(), account.getLastName(),
                account.getPhone(), account.getDob(), account.getRole());
        this.numSuccess = numSuccess;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.addresses = addresses;
    }

    public int getNumSuccess() {
        return numSuccess;
    }

    public void setNumSuccess(int numSuccess) {
        this.numSuccess = numSuccess;
    }

    public int getNumCancel() {
        return numCancel;
    }

    public void setNumCancel(int numCancel) {
        this.numCancel = numCancel;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Customer{name=" + this.getLastName() + " " + this.getMiddleName() + " " + this.getFirstName() + '}';
    }

}
