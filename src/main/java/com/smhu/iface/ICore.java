package com.smhu.iface;

import com.smhu.order.Order;

public interface ICore {

    public void preProcessMapFilterOrder(String market, Order order);
    
    public void scanShippers();
    
    public void filterOrder(Order order);
    
    public void scanOrder();
}
