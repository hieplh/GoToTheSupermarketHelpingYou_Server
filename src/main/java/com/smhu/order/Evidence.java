package com.smhu.order;

import java.sql.Date;
import java.sql.Time;

public class Evidence {

    private String id;
    private String image;
    private Date createDate;
    private Time createTime;

    public Evidence() {
    }

    public Evidence(String id, String image, Date createDate, Time createTime) {
        this.id = id;
        this.image = image;
        this.createDate = createDate;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
