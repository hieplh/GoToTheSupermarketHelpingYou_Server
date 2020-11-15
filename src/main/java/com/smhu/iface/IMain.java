package com.smhu.iface;

import com.smhu.entity.Market;
import com.smhu.order.Order;
import com.smhu.status.Status;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IMain {

    Map<String, String> loadRoles() throws SQLException, ClassNotFoundException;

    List<Status> loadStatus() throws SQLException, ClassNotFoundException;

    List<Market> loadMarket() throws SQLException, ClassNotFoundException;

    List<Order> loadOrder(Date date, String status) throws SQLException, ClassNotFoundException;

//    Order getOrderById(String orderId) throws SQLException, ClassNotFoundException;
    
    void loadOrderDetail(Order order) throws SQLException, ClassNotFoundException;
    
    void loadOrderDetail(List<Order> listOrders) throws SQLException, ClassNotFoundException;
}
