package com.smhu.account;

public class ShipperUpdateMaxNumOrder {

    private String username;
    private String role;
    private int maxNumOrder;

    public ShipperUpdateMaxNumOrder() {
    }

    public ShipperUpdateMaxNumOrder(String username, String role, int maxNumOrder) {
        this.username = username;
        this.role = role;
        this.maxNumOrder = maxNumOrder;
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

    public int getMaxNumOrder() {
        return maxNumOrder;
    }

    public void setMaxNumOrder(int maxNumOrder) {
        this.maxNumOrder = maxNumOrder;
    }

    @Override
    public String toString() {
        return "ShipperUpdateMaxNumOrder{" + "username=" + username + ", role=" + role + ", maxNumOrder=" + maxNumOrder + '}';
    }

}
