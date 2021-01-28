package com.smhu.helper;

import com.smhu.google.geocoding.Geocoding;
import com.smhu.google.geocoding.Result;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.response.customer.OrderResponseCustomer;
import com.smhu.response.shipper.OrderDelivery;
import com.smhu.response.shipper.OrderDoneDelivery;
import com.smhu.response.shipper.OrderShipper;
import com.smhu.url.UrlConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncHelper {

    public OrderResponseCustomer syncOrderSystemToOrderResponseCustomer(Order order) {
        OrderResponseCustomer obj = new OrderResponseCustomer();
        obj.setId(order.getId());
        obj.setMarket(order.getMarket());
        obj.setQuantity(order.getDetails().size());
        obj.setTotalCost(order.getTotalCost());
        obj.setTimeCreate(order.getCreateTime());
        obj.setTimeDelivery(order.getTimeDelivery());
        obj.setStatus(order.getStatus());
        obj.setShipper(order.getShipper());
        return obj;
    }

    public OrderDelivery syncOrderSystemToOrderDelivery(Order order) {
        OrderDelivery obj = new OrderDelivery();
        obj.setId(order.getId());
        obj.setCust(order.getCust());
        obj.setAddressDelivery(convertAddressToMap(order.getAddressDelivery(), 0));
        obj.setMarket(order.getMarket());
        obj.setNote(order.getNote());
        obj.setShipper(order.getShipper().getUsername());
        obj.setStatus(order.getStatus());
        obj.setCostShopping(order.getCostShopping());
        obj.setCostDelivery(order.getCostDelivery());
        obj.setTotalCost(order.getTotalCost());
        obj.setDateDelivery(order.getDateDelivery());
        obj.setTimeDelivery(order.getTimeDelivery());
        obj.setDetails(order.getDetails());
        return obj;
    }
    
    public OrderShipper syncOrderSystemToOrderShipper(Order order) {
        OrderShipper obj = new OrderShipper();
        obj.setId(order.getId());
        obj.setCust(order.getCust());
        obj.setAddressDelivery(convertAddressToMap(order.getAddressDelivery(), 0));
        obj.setMarket(order.getMarket());
        obj.setNote(order.getNote());
        obj.setShipper(order.getShipper().getUsername());
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
        obj.setMarket(order.getMarket());
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

    private Geocoding getGeoding(List<String> addresses) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionConvertPhysicalAddressToGeocoding(addresses), "utf-8"),
                Geocoding.class);
    }

    public Map<String, String> convertAddressToMap(String address, int count) {
        Map<String, String> map = new HashMap<>();
        if (count > 1) {
            map.put("address", address);
        } else {
            List<String> addresses = new ArrayList<>();
            addresses.add(address);
            try {
                Geocoding geocoding = getGeoding(addresses);
                List<Result> results = ExtractLocationGeocoding.getResults(geocoding);
                for (Result result : results) {
                    Map<String, String> location = ExtractLocationGeocoding.getMapLocation(result);
                    for (Map.Entry<String, String> entry : location.entrySet()) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (IOException e) {
                convertAddressToMap(address, count + 1);
            }
            map.put("address", address);
        }
        return map;
    }
}
