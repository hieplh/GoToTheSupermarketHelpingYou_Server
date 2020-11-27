package com.smhu.iface;

import com.smhu.order.Order;

public interface ICore {

    public void scanShippers();
    
    public void filterOrder(Order order);
    
    public void scanOrder();
}
