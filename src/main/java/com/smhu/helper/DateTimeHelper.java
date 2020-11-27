package com.smhu.helper;

import com.smhu.order.Order;
import com.smhu.system.SystemTime;
import java.sql.Time;
import java.time.LocalTime;

public class DateTimeHelper {

    public int parseTimeToMinute(LocalTime localTime) {
        return localTime != null ? localTime.getHour() * 60 + localTime.getMinute() : 5;
    }

    public boolean calculateTimeForShipper(Order order, int timeTravel) {
        int systemTime = parseTimeToMinute(new Time(SystemTime.SYSTEM_TIME).toLocalTime());
        int calculateTimeDelivery = parseTimeToMinute(order.getTimeDelivery().toLocalTime()) - timeTravel;
        int timeDelivery = parseTimeToMinute(order.getTimeDelivery().toLocalTime());
        if (timeTravel == 180) {
            return systemTime >= calculateTimeDelivery && systemTime < timeDelivery;
        }
        return systemTime >= calculateTimeDelivery - 30 && systemTime < timeDelivery;
    }
}
