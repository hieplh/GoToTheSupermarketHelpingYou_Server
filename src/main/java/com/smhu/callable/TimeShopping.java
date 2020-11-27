package com.smhu.callable;

import com.smhu.order.Order;
import com.smhu.order.OrderDetail;

import java.util.List;
import java.util.concurrent.Callable;

public class TimeShopping<T> implements Callable<T> {

    private final List<Order> listOrdersForShipper;

    public TimeShopping(List<Order> listOrdersForShipper) {
        this.listOrdersForShipper = listOrdersForShipper;
    }

    @Override
    public T call() throws Exception {
        int timeShopping = 0;
        synchronized (listOrdersForShipper) {
            for (Order order : listOrdersForShipper) {
                timeShopping += getTotalTimeShopping(order);
            }
        }
        return (T) ((Integer) timeShopping);
    }

    private int getTotalTimeShopping(Order order) {
        if (order.getDetails() == null) {
            return 10;
        }

        double weightDefault = 0.5;
        int timeDefault = 10;
        int totalTimeShopping = 0;
        for (OrderDetail orderDetail : order.getDetails()) {
            totalTimeShopping += timeDefault;
            double weight = orderDetail.getWeight();
            totalTimeShopping += (int) (weight / weightDefault) >= 1 ? (int) (weight / weightDefault - 1) * 5 : 0;
        }
        return totalTimeShopping;
    }
}
