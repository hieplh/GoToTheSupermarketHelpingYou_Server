package com.smhu.overall;

import com.smhu.order.PartOrder;
import java.util.List;

public class SystemAmount {

    private String desc;
    private int countOrder;
    private double amountShipping;
    private double amountShopping;
    private double amountTotal;

    public SystemAmount() {
    }

    public SystemAmount(List<PartOrder> orders) {
        if (orders != null) {
            this.countOrder = orders.size();
            orders.forEach(o -> this.amountShipping += o.getCostDelivery() * o.getCommissionShipping() / 100.0);
            orders.forEach(o -> this.amountShopping += o.getCostShopping() * o.getCommissionShopping() / 100.0);
            amountTotal = amountShipping + amountShopping;
        }
    }

    public SystemAmount(String desc, int countOrder, double amountShipping, double amountShopping, double amountTotal) {
        this.desc = desc;
        this.countOrder = countOrder;
        this.amountShipping = amountShipping;
        this.amountShopping = amountShopping;
        this.amountTotal = amountTotal;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCountOrder() {
        return countOrder;
    }

    public void setCountOrder(int countOrder) {
        this.countOrder = countOrder;
    }

    public double getAmountShipping() {
        return amountShipping;
    }

    public void setAmountShipping(double amountShipping) {
        this.amountShipping = amountShipping;
    }

    public double getAmountShopping() {
        return amountShopping;
    }

    public void setAmountShopping(double amountShopping) {
        this.amountShopping = amountShopping;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    @Override
    public String toString() {
        return "SystemAmount{" + "desc=" + desc + ", countOrder=" + countOrder + ", amountShipping=" + amountShipping + ", amountShopping=" + amountShopping + ", amountTotal=" + amountTotal + '}';
    }

}
