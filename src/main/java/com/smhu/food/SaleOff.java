package com.smhu.food;

import java.sql.Date;
import java.sql.Time;

public class SaleOff {

    Date startDate;
    Date endDate;
    Time startTime;
    Time endTime;
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

    @Override
    public String toString() {
        return "SaleOff{" + "startDate=" + startDate + ", endDate=" + endDate + ", startTime=" + startTime + ", endTime=" + endTime + ", saleOff=" + saleOff + '}';
    }

}
