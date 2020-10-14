package com.smhu.helper;

import com.smhu.order.Order;
import com.smhu.order.TimeTravel;
import java.time.LocalTime;

public class DateTimeHelper {

    public static int parseTimeToMinute(LocalTime localTime) {
        return localTime.getHour() * 60 + localTime.getMinute();
    }

    public int calculateTimeForShipper(Order order, TimeTravel timeTravel) {
        System.out.println("Time Delivery: " + order.getTimeDelivery().toString());
        int totalMinuteDeliveryDeadline = parseTimeToMinute(order.getTimeDelivery().toLocalTime());

        int totalMinuteGoing = parseTimeToMinute(timeTravel.getGoing().toLocalTime());
        int totalMinuteShopping = parseTimeToMinute(timeTravel.getShopping().toLocalTime());
        int totalMinuteDelivery = parseTimeToMinute(timeTravel.getDelivery().toLocalTime());
        int totalMinuteTraffic = parseTimeToMinute(timeTravel.getTraffic().toLocalTime());

        return totalMinuteDeliveryDeadline - totalMinuteGoing - totalMinuteShopping - totalMinuteDelivery - totalMinuteTraffic;
    }
}
