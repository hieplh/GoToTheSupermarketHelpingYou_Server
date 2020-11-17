package com.smhu.transaction;

import java.sql.Date;
import java.sql.Time;

public class Transaction {

    private String idTrans;
    private String name;
    private double amount;
    private String desc;
    private int status;
    private String idOrder;
    private Date createDate;
    private Time createTime;

    public Transaction() {
    }

    public Transaction(String idTrans, String name, double amount, String desc, int status, String idOrder, Date createDate, Time createTime) {
        this.idTrans = idTrans;
        this.name = name;
        this.amount = amount;
        this.desc = desc;
        this.status = status;
        this.idOrder = idOrder;
        this.createDate = createDate;
        this.createTime = createTime;
    }

    public String getIdTrans() {
        return idTrans;
    }

    public void setIdTrans(String idTrans) {
        this.idTrans = idTrans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Time getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Time createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Transaction{" + "idTrans=" + idTrans + ", name=" + name + ", amount=" + amount + ", desc=" + desc + ", status=" + status + ", idOrder=" + idOrder + ", createDate=" + createDate + ", createTime=" + createTime + '}';
    }

}
