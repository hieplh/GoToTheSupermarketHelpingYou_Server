package com.capstone.account;

import java.sql.Date;

public class Account {

    String id;
    String username;
    String password;
    String email;
    int role;
    Date createDate;
    Date lastLogin;
    Date lastUpdate;
    int numOrder;
    int numCancel;
    double wallet;
    int status;
    boolean isActive;
    Profile profile;

    public Account() {
    }

    public Account(String username, String email, int role, int status, Profile profile) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.profile = profile;
    }

    public Account(String id, String username, String password, String email, int role, Date createDate, Date lastLogin, Date lastUpdate, int numOrder, int numCancel, double wallet, int status, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.createDate = createDate;
        this.lastLogin = lastLogin;
        this.lastUpdate = lastUpdate;
        this.numOrder = numOrder;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.status = status;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setNumOrder(int numOrder) {
        this.numOrder = numOrder;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
