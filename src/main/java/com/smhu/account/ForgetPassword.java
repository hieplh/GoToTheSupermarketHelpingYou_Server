package com.smhu.account;

import java.util.Objects;

public class ForgetPassword {

    private String username;
    private String newPwd;
    private String role;
    
    public ForgetPassword(String username, String newPwd, String role) {
        this.username = username;
        this.newPwd = newPwd;
        this.role = role;
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

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    @Override
    public String toString() {
        return "ForgetPassword{" + "username=" + username + ", newPwd=" + newPwd + ", role=" + role + '}';
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
        final ForgetPassword other = (ForgetPassword) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

}
