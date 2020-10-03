package com.capstone.account;

import java.sql.Date;

public class Account {

    private String id;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Date dob;
    private int role;
    private Date createDate;
    private int numOrder;
    private int numCancel;
    private double wallet;
    private int status;
    private boolean isActive;

    public Account() {
    }

    public Account(String id, String username, String password, String firstName, String middleName, String lastName, String email, String phone, Date dob, int role, Date createDate, int numOrder, int numCancel, double wallet, int status, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.role = role;
        this.createDate = createDate;
        this.numOrder = numOrder;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.status = status;
        this.isActive = isActive;
    }    

    public Account(String id, String firstName, String middleName, String lastName, String email, String phone, Date dob,
            int role, int numOrder, int numCancel, double wallet, int status) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.role = role;
        this.numOrder = numOrder;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public Date getDob() {
        return dob;
    }

    public int getNumOrder() {
        return numOrder;
    }

    public int getNumCancel() {
        return numCancel;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

}
