package com.smhu.statistic;

public class Week {

    private String week;
    private String fromdate;
    private double amountEarned;
    private double amountCharged;

    public Week() {
    }

    public Week(String week, String fromdate, double amountEarned, double amountCharged) {
        this.week = week;
        this.fromdate = fromdate;
        this.amountEarned = amountEarned;
        this.amountCharged = amountCharged;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public double getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(double amountEarned) {
        this.amountEarned = amountEarned;
    }

    public double getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(double amountCharged) {
        this.amountCharged = amountCharged;
    }

    @Override
    public String toString() {
        return "Week{" + "week=" + week + ", fromdate=" + fromdate + ", amountEarned=" + amountEarned + ", amountCharged=" + amountCharged + '}';
    }

}
