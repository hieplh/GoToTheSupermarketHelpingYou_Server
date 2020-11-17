package com.smhu.helper;

import com.smhu.controller.MarketController;
import com.smhu.controller.ShipperController;
import com.smhu.iface.IShipper;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.response.customer.OrderResponseCustomer;
import com.smhu.response.shipper.OrderDoneDelivery;
import com.smhu.response.shipper.OrderShipper;

public class SyncHelper {

    public OrderResponseCustomer syncOrderSystemToOrderResponseCustomer(Order order) {
        IShipper shipperListener = new ShipperController().getShipperListener();

        OrderResponseCustomer obj = new OrderResponseCustomer();
        obj.setId(order.getId());
        obj.setMarket(MarketController.mapMarket.get(order.getMarket()));
        obj.setQuantity(order.getDetails().size());
        obj.setTotalCost(order.getTotalCost());
        obj.setTimeCreate(order.getCreateTime());
        obj.setTimeDelivery(order.getTimeDelivery());
        obj.setStatus(order.getStatus());
        obj.setShipper(shipperListener.getShipper(order.getShipper()));
        return obj;
    }

    public OrderShipper syncOrderSystemToOrderShipper(Order order) {
        OrderShipper obj = new OrderShipper();
        obj.setId(order.getId());
        obj.setCust(order.getCust());
        obj.setAddressDelivery(order.getAddressDelivery());
        obj.setMarket(MarketController.mapMarket.get(order.getMarket()));
        obj.setNote(order.getNote());
        obj.setShipper(order.getShipper());
        obj.setStatus(order.getStatus());
        obj.setCostShopping(order.getCostShopping());
        obj.setCostDelivery(order.getCostDelivery());
        obj.setTotalCost(order.getTotalCost());
        obj.setDateDelivery(order.getDateDelivery());
        obj.setTimeDelivery(order.getTimeDelivery());
        obj.setDetails(order.getDetails());
        return obj;
    }

    public OrderDoneDelivery syncOrderSystemToOrderDoneDelivery(Order order) {
        OrderDoneDelivery obj = new OrderDoneDelivery();
        obj.setId(order.getId());
        obj.setMarketName(MarketController.mapMarket.get(order.getMarket()).getName());
        obj.setQuantity(order.getDetails().size());
        obj.setCostDelivery(order.getCostDelivery());
        obj.setCostShopping(order.getCostShopping());
        
        double totalCostItems = 0;
        for (OrderDetail detail : order.getDetails()) {
            totalCostItems += detail.getPricePaid();
        }
        obj.setTotalCostItems(totalCostItems);
        
        obj.setTotalCost(order.getTotalCost());
        obj.setCreateDate(order.getDateDelivery());
        obj.setEndTime(order.getLastUpdate());
        obj.setStatus(order.getStatus());
        obj.setNote(order.getNote());
        return obj;
    }
}
