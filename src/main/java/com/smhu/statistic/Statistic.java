package com.smhu.statistic;

import java.util.List;

public class Statistic {

    private String ofMonthYear;
    private String fromDate;
    private NumberOfOrders numberOfOrders;
    private Amount amount;
    private List<Week> weeks;

    public Statistic() {
    }

    public Statistic(String ofMonthYear, String fromDate, NumberOfOrders numberOfOrders, Amount amount, List<Week> weeks) {
        this.ofMonthYear = ofMonthYear;
        this.fromDate = fromDate;
        this.numberOfOrders = numberOfOrders;
        this.amount = amount;
        this.weeks = weeks;
    }

    public String getOfMonthYear() {
        return ofMonthYear;
    }

    public void setOfMonthYear(String ofMonthYear) {
        this.ofMonthYear = ofMonthYear;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public NumberOfOrders getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(NumberOfOrders numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    @Override
    public String toString() {
        return "Statistic{" + "ofMonthYear=" + ofMonthYear + ", fromDate=" + fromDate + ", numberOfOrders=" + numberOfOrders + ", amount=" + amount + ", weeks=" + weeks + '}';
    }

}
