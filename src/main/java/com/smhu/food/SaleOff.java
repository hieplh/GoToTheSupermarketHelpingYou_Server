/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smhu.food;

import com.google.gson.annotations.SerializedName;
import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Admin
 */
public class SaleOff {

    @SerializedName("startDate")
    Date startDate;

    @SerializedName("endDate")
    Date endDate;

    @SerializedName("startTime")
    Time startTime;

    @SerializedName("EndTime")
    Time endTime;

    @SerializedName("discount")
    int saleOff;

    public SaleOff() {
    }

    public SaleOff(Date startDate, Date endDate, Time startTime, Time endTime, int saleOff) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.saleOff = saleOff;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getSaleOff() {
        return saleOff;
    }

    public void setSaleOff(int saleOff) {
        this.saleOff = saleOff;
    }

}
