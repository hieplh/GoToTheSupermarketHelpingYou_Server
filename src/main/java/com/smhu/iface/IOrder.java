package com.smhu.iface;

import com.smhu.account.Shipper;
import com.smhu.account.ShipperAlter;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import java.sql.SQLException;
import java.util.List;

public interface IOrder {

    public Order getOrderById(String orderId, String type) throws SQLException, ClassNotFoundException;

    public List<ShipperAlter> getListShipperAlter(String orderId) throws SQLException, ClassNotFoundException;

    public String insertAlterShipper(String orderId, String oldShipper, String newShipper, String author) throws SQLException, ClassNotFoundException;

    public String insertRecordOrder(Order order, Shipper shipper) throws SQLException, ClassNotFoundException;

    public String insertOrder(Order order) throws SQLException, ClassNotFoundException;

    public int[] insertOrderDetail(String idOrder, List<OrderDetail> details) throws SQLException, ClassNotFoundException;

    public String updatetOrder(Order order) throws SQLException, ClassNotFoundException;

    public String CancelOrder(Order order) throws SQLException, ClassNotFoundException;
}
