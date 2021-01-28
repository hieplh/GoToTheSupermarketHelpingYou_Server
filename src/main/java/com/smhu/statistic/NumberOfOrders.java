package com.smhu.statistic;

public class NumberOfOrders {

    private int numOrders;
    private int numRejected;
    private int numCanceled;
    private int numDone;

    public NumberOfOrders() {
    }

    public NumberOfOrders(int numOrders, int numRejected, int numCanceled, int numDone) {
        this.numOrders = numOrders;
        this.numRejected = numRejected;
        this.numCanceled = numCanceled;
        this.numDone = numDone;
    }

    public int getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(int numOrders) {
        this.numOrders = numOrders;
    }

    public int getNumRejected() {
        return numRejected;
    }

    public void setNumRejected(int numRejected) {
        this.numRejected = numRejected;
    }

    public int getNumCanceled() {
        return numCanceled;
    }

    public void setNumCanceled(int numCanceled) {
        this.numCanceled = numCanceled;
    }

    public int getNumDone() {
        return numDone;
    }

    public void setNumDone(int numDone) {
        this.numDone = numDone;
    }

    @Override
    public String toString() {
        return "NumberOfOrders{" + "numOrders=" + numOrders + ", numRejected=" + numRejected + ", numCanceled=" + numCanceled + ", numDone=" + numDone + '}';
    }

}
