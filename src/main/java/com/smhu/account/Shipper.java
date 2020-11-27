package com.smhu.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

public class Shipper extends Account {

    private int numDelivery;
    private int numCancel;
    private double wallet;
    private int maxOrder;
    private String lat;
    private String lng;
    private double rating;

    @JsonIgnore
    private String tokenFCM;

    public Shipper() {
    }

    public Shipper(Account account) {
        super(account.getId(), account.getUsername(), account.getFirstName(), account.getMiddleName(), account.getLastName(),
                account.getEmail(), account.getPhone(), account.getDob(), account.getRole());
    }

    public Shipper(Account account, int numDelivery, int numCancel, int maxOrder, double wallet, double rating, String lat, String lng, String tokenFCM) {
        super(account.getId(), account.getUsername(), account.getFirstName(), account.getMiddleName(), account.getLastName(),
                account.getEmail(), account.getPhone(), account.getDob(), account.getRole());
        this.maxOrder = maxOrder;
        this.numDelivery = numDelivery;
        this.numCancel = numCancel;
        this.wallet = wallet;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.tokenFCM = tokenFCM;
    }

    public int getNumDelivery() {
        return numDelivery;
    }

    public void setNumDelivery(int numDelivery) {
        this.numDelivery = numDelivery;
    }

    public int getNumCancel() {
        return numCancel;
    }

    public void setNumCancel(int numCancel) {
        this.numCancel = numCancel;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public int getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(int maxOrder) {
        this.maxOrder = maxOrder;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }

    @Override
    public String toString() {
        return "Shipper{" + "id=" + this.getId() + ", lat=" + lat + ", lng=" + lng + ", rating=" + rating + ", tokenFCM=" + tokenFCM + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Shipper other = (Shipper) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }
}
