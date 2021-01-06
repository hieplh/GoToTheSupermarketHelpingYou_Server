package com.smhu.account;

import java.sql.Date;
import java.util.Objects;

public class AccountRegister {

    private String username;
    private String password;
    private String role;
    private String fullname;
    private Date dob;
    private String vin;
    private String codeOTP;

    public AccountRegister(String username, String password, String role, String fullname, Date dob, String vin, String codeOTP) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.dob = dob;
        this.vin = vin;
        this.codeOTP = codeOTP;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCodeOTP() {
        return codeOTP;
    }

    public void setCodeOTP(String codeOTP) {
        this.codeOTP = codeOTP;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountRegister other = (AccountRegister) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccountRegister{" + "username=" + username + ", password=" + password + ", role=" + role + ", fullname=" + fullname + ", dob=" + dob + ", vin=" + vin + ", codeOTP=" + codeOTP + '}';
    }

}
