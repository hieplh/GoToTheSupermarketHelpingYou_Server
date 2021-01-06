package com.smhu.order;

import com.smhu.account.Customer;
import com.smhu.account.Shipper;
import java.sql.Date;
import java.sql.Time;

public class OrderRecording {

    private Order order;
    private Customer cust;
    private Shipper shipper;
    private Date date;
    private Time time;

    public OrderRecording() {
    }

    public OrderRecording(Order order, Customer cust, Shipper shipper, Date date, Time time) {
        this.order = order;
        this.cust = cust;
        this.shipper = shipper;
        this.date = date;
        this.time = time;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Customer getCust() {
        return cust;
    }

    public void setCust(Customer cust) {
        this.cust = cust;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OrderRecording{" + "order=" + order + ", cust=" + cust + ", shipper=" + shipper + ", date=" + date + ", time=" + time + '}';
    }

}
