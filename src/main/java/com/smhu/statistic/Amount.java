package com.smhu.statistic;

public class Amount {

    private double amountRefund;
    private double amountTotal;
    private double amountCharged;
    private double amountEarned;

    public Amount(double amountRefund, double amountTotal, double amountCharged, double amountEarned) {
        this.amountRefund = amountRefund;
        this.amountTotal = amountTotal;
        this.amountCharged = amountCharged;
        this.amountEarned = amountEarned;
    }

    public Amount() {
    }

    public double getAmountRefund() {
        return amountRefund;
    }

    public void setAmountRefund(double amountRefund) {
        this.amountRefund = amountRefund;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public double getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(double amountCharged) {
        this.amountCharged = amountCharged;
    }

    public double getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(double amountEarned) {
        this.amountEarned = amountEarned;
    }

    @Override
    public String toString() {
        return "Amount{" + "amountRefund=" + amountRefund + ", amountTotal=" + amountTotal + ", amountCharged=" + amountCharged + ", amountEarned=" + amountEarned + '}';
    }

}
