package com.smhu.comparator;

import com.smhu.order.Order;
import java.util.Comparator;

public class SortByTimeDelivery implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        return o1.getTimeDelivery().compareTo(o2.getTimeDelivery());
    }

}
