package com.smhu.market;

import java.io.Serializable;

public class Market implements Serializable {

    private String id;
    private String name;
    private String addr1;
    private String addr2;
    private String addr3;
    private String addr4;
    private String lat;
    private String lng;
    private String image;

    public Market() {
    }

    public Market(String id, String name, String addr1, String addr2, String addr3, String addr4, String lat, String lng, String image) {
        this.id = id;
        this.name = name;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.addr3 = addr3;
        this.addr4 = addr4;
        this.lat = lat;
        this.lng = lng;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Market{" + "id=" + id + ", name=" + name + ", addr1=" + addr1 + ", addr2=" + addr2 + ", addr3=" + addr3 + ", addr4=" + addr4 + ", lat=" + lat + ", lng=" + lng + ", image=" + image + '}';
    }
}
