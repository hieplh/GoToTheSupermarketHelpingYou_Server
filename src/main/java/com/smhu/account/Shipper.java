package com.smhu.account;

import java.sql.Date;

public class Shipper {

    private String id;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Date dob;
    private String fromRoute;
    private String toRoute;
    private int numDelivery;
    private int numCancel;
    private double wallet;
    private int status;
    private Date createDate;

    public Shipper() {
    }

    public Shipper(String id, String username, String password, String firstName, String middleName, String lastName, 
            String email, String phone, Date dob, String fromRoute, String toRoute, 
            int numDelivery, int numCancel, double wallet, int status, Date createDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.fromRoute = fromRoute;
        this.toRoute = toRoute;
        this.numDelivery = numDelivery;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.status = status;
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getFromRoute() {
        return fromRoute;
    }

    public void setFromRoute(String fromRoute) {
        this.fromRoute = fromRoute;
    }

    public String getToRoute() {
        return toRoute;
    }

    public void setToRoute(String toRoute) {
        this.toRoute = toRoute;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNumDelivery(int numDelivery) {
        this.numDelivery = numDelivery;
    }

    public void setNumCancel(int numCancel) {
        this.numCancel = numCancel;
    }

}
