package com.smhu.callable;

import com.smhu.controller.MarketController;
import com.smhu.entity.Market;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.google.matrixobj.MatrixObject;
import com.smhu.helper.ExtractElementDistanceMatrixApi;
import com.smhu.helper.GsonHelper;
import com.smhu.order.Order;
import com.smhu.url.UrlConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TimeDelivery<T> implements Callable<T> {

    private final List<Order> listOrdersForShipper;

    public TimeDelivery(List<Order> listOrdersForShipper) {
        this.listOrdersForShipper = listOrdersForShipper;
    }

    @Override
    public T call() throws Exception {
        int count = 0;
        int result = 0;
        synchronized (listOrdersForShipper) {
            do {
                result += getTheLeastTimeMoving(listOrdersForShipper, count);
                ++count;
            } while (count < listOrdersForShipper.size());
        }

        return (T) ((Integer) result);
    }

    private List<String> getListPhysicalAddresses(List<Order> listOrders) {
        List<String> list = new ArrayList();
        for (Order order : listOrders) {
            list.add(order.getAddressDelivery());
        }
        return list;
    }

    private MatrixObject getMatrixObject(String source, List<String> destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }

    private MatrixObject getMatrixObject(String[] source, List<String> destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }

    private void swap(List<Order> listOrders, int swap1, int swap2) {
        if (swap1 == swap2) {
            return;
        }

        Order tmp = listOrders.get(swap1);
        listOrders.set(swap1, listOrders.get(swap2));
        listOrders.set(swap2, tmp);
    }

    private int getTheLeastTimeMoving(List<Order> listOrders, int count) throws IOException {
        MatrixObject matrixObj;
        ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
        if (count > 0) {
            Order order = listOrders.get(count - 1);
            matrixObj = getMatrixObject(order.getAddressDelivery(), getListPhysicalAddresses(listOrders.subList(count, listOrders.size())));
        } else {
            Market market = MarketController.mapMarket.get(listOrders.get(count).getMarket());
            matrixObj = getMatrixObject(new String[]{market.getLat(), market.getLng()}, getListPhysicalAddresses(listOrders));
        }

        List<ElementObject> listElements = extract.getListElements(matrixObj);
        List<String> listDistanceValue = extract.getListDistance(listElements, "value");
        List<String> listDurationValue = extract.getListDuration(listElements, "value");

        int pos = 0;
        for (int i = 1; i < listDistanceValue.size(); i++) {
            if (Integer.parseInt(listDistanceValue.get(pos)) > Integer.parseInt(listDistanceValue.get(i))) {
                pos = i;
            }
        }
        swap(listOrders, count, pos + count);

        return (Integer.parseInt(listDurationValue.get(pos)) / 60) + 1;
    }
}
