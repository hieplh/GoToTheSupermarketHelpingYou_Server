package com.smhu.entity;

import java.io.Serializable;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EntityListeners;
//import javax.persistence.FetchType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//@Entity
//@Table(name = "MARKET")
//@EntityListeners(AuditingEntityListener.class)
public class Market implements Serializable {

//    @Id
//    @Column(name = "ID")
    private String id;

//    @Column(name = "NAME")
    private String name;

//    @Column(name = "ADDR_1")
    private String addr1;

//    @Column(name = "ADDR_2")
    private String addr2;

//    @Column(name = "ADDR_3")
    private String addr3;

//    @Column(name = "ADDR_4")
    private String addr4;

//    @Column(name = "LAT")
    private String lat;

//    @Column(name = "LNG")
    private String lng;

//    @Column(name = "IMAGE")
    private String image;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    @JoinColumn(name = "CORPORATION")
//    private Corporation corporation;

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

//    public Corporation getCorporation() {
//        return corporation;
//    }
//
//    public void setCorporation(Corporation corporation) {
//        this.corporation = corporation;
//    }

    @Override
    public String toString() {
        return "Market{" + "id=" + id + ", name=" + name + ", addr1=" + addr1 + ", addr2=" + addr2 + ", addr3=" + addr3 + ", addr4=" + addr4 + ", lat=" + lat + ", lng=" + lng + ", image=" + image + '}';
    }
}
