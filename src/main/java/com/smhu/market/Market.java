package com.smhu.market;

import com.google.gson.annotations.SerializedName;

public class Market {

    String id;
    
    @SerializedName("name")
    String name;
    
    @SerializedName("addr1")
    String addr1;
    
    @SerializedName("addr2")
    String addr2;
    
    @SerializedName("addr3")
    String addr3;
    
    @SerializedName("addr4")
    String addr4;
    
    @SerializedName("lat")
    String lat;
    
    @SerializedName("lng")
    String lng;

    public Market() {
    }

    public Market(String id, String name, String addr1, String addr2, String addr3, String addr4, String lat, String lng) {
        this.id = id;
        this.name = name;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.addr3 = addr3;
        this.addr4 = addr4;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddr1() {
        return addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public String getAddr3() {
        return addr3;
    }

    public String getAddr4() {
        return addr4;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
    
    public void setLng(String lng) {
        this.lng = lng;
    }
}
