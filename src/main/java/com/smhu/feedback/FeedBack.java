package com.smhu.feedback;

public class FeedBack {

    private String id;
    private String shipper;
    private String order;
    private String feedback;
    private int rating;

    public FeedBack() {
    }

    public FeedBack(String id, String shipper, String order, String feedback, int rating) {
        this.id = id;
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
        return "FeedBack{" + "id=" + id + ", shipper=" + shipper + ", order=" + order + ", feedback=" + feedback + ", rating=" + rating + '}';
    }

}
