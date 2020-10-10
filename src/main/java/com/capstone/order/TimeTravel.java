package com.capstone.order;

import java.sql.Time;

public class TimeTravel {

    private Time going;
    private Time shopping;
    private Time delivery;
    private Time traffic;

    public TimeTravel() {
    }

    public TimeTravel(Time going, Time shopping, Time delivery, Time traffic) {
        this.going = going;
        this.shopping = shopping;
        this.delivery = delivery;
        this.traffic = traffic;
    }

    public Time getGoing() {
        return going;
    }

    public void setGoing(Time going) {
        this.going = going;
    }

    public Time getShopping() {
        return shopping;
    }

    public void setShopping(Time shopping) {
        this.shopping = shopping;
    }

    public Time getDelivery() {
        return delivery;
    }

    public void setDelivery(Time delivery) {
        this.delivery = delivery;
    }

    public Time getTraffic() {
        return traffic;
    }

    public void setTraffic(Time traffic) {
        this.traffic = traffic;
    }

    @Override
    public String toString() {
        return "TimeTravel{" + "going=" + going + ", shopping=" + shopping + ", delivery=" + delivery + ", traffic=" + traffic + '}';
    }

}
