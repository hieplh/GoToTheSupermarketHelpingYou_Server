package com.smhu.account;

import java.sql.Date;
import java.sql.Time;

public class ShipperAlter {

    private String id;
    private Shipper original;
    private Shipper alternative;
    private Account author;
    private Date createDate;
    private Time createTime;

    public ShipperAlter() {
    }

    public ShipperAlter(String id, Shipper original, Shipper alternative, Account author, Date createDate, Time createTime) {
        this.id = id;
        this.original = original;
        this.alternative = alternative;
        this.author = author;
        this.createDate = createDate;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Shipper getOriginal() {
        return original;
    }

    public void setOriginal(Shipper original) {
        this.original = original;
    }

    public Shipper getAlternative() {
        return alternative;
    }

    public void setAlternative(Shipper alternative) {
        this.alternative = alternative;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
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
        return "ShipperAlter{" + "id=" + id + ", original=" + original + ", alternative=" + alternative + ", author=" + author + ", createDate=" + createDate + ", createTime=" + createTime + '}';
    }

}
