package com.smhu.order;

import com.smhu.controller.MarketController;
import com.smhu.market.Market;
import com.google.gson.annotations.SerializedName;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrderDelivery {

    @SerializedName("distance")
    private String distance;

    @SerializedName("order")
    private Order order;

    public OrderDelivery() {
    }

    public OrderDelivery(String distance, com.smhu.order.Order order) {
        this.distance = distance;
        this.order = new Order(order.getId(),
                order.getCust(),
                MarketController.mapMarket.get(order.getMarket()),
                order.getNote(),
                order.getCostShopping(),
                order.getCostDelivery(),
                order.getTotalCost(),
                order.getDateDelivery(),
                order.getTimeDelivery(),
                order.getDetails());
    }

    public String getDistance() {
        return distance;
    }

    public Order getOrder() {
        return order;
    }

    class Order {

        @SerializedName("id")
        private String id;              //server

        @SerializedName("cust")
        private String cust;            //cust

        @SerializedName("market")
        private Market market;            //cust

        @SerializedName("note")
        private String note;            //cust

        @SerializedName("costShipping")
        private double costShopping;    //cust

        @SerializedName("costDelivery")
        private double costDelivery;    //cust

        @SerializedName("totalCost")
        private double totalCost;       //cust

        @SerializedName("dateDelivery")
        private Date dateDelivery;      //cust

        @SerializedName("timeDelivery")
        private Time timeDelivery;      //cust

        @SerializedName("details")
        private List<OrderDetail> details;

        public Order() {
        }

        public Order(String id, String cust, Market market, String note, double costShopping, double costDelivery, double totalCost, Date dateDelivery, Time timeDelivery, List<OrderDetail> details) {
            this.id = id;
            this.cust = cust;
            this.market = market;
            this.note = note;
            this.costShopping = costShopping;
            this.costDelivery = costDelivery;
            this.totalCost = totalCost;
            this.dateDelivery = dateDelivery;
            this.timeDelivery = timeDelivery;
            this.details = details;
        }

        public String getId() {
            return id;
        }

        public String getCust() {
            return cust;
        }

        public Market getMarket() {
            return market;
        }

        public String getNote() {
            return note;
        }

        public double getCostShopping() {
            return costShopping;
        }

        public double getCostDelivery() {
            return costDelivery;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public Date getDateDelivery() {
            return dateDelivery;
        }

        public Time getTimeDelivery() {
            return timeDelivery;
        }

        public List<OrderDetail> getDetails() {
            return details;
        }

    }
}
