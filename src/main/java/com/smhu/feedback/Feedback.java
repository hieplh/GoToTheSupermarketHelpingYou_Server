package com.smhu.feedback;

public class Feedback {

    private String id;
    private String customer;
    private String shipper;
    private String order;
    private String feedback;
    private int rating;

    public Feedback() {
    }

    public Feedback(String id, String customer, String shipper, String order, String feedback, int rating) {
        this.id = id;
        this.customer = customer;
        this.shipper = shipper;
        this.order = order;
        this.feedback = feedback;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "FeedBack{" + "id=" + id + ", customer=" + customer + ", shipper=" + shipper + ", order=" + order + ", feedback=" + feedback + ", rating=" + rating + '}';
    }
}
