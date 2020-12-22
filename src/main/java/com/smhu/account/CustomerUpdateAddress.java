package com.smhu.account;

import java.util.List;

public class CustomerUpdateAddress {

    private String username;
    private String role;
    private List<Address> addresses;

    public CustomerUpdateAddress() {
    }

    public CustomerUpdateAddress(String username, String role, List<Address> addresses) {
        this.username = username;
        this.role = role;
        this.addresses = addresses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "CustomerUpdateAddress{" + "username=" + username + ", role=" + role + ", addresses=" + addresses + '}';
    }

}
