package com.capstone.order;

import java.sql.Timestamp;

public class Evidence {

    private String id;
    private String image;
    private Timestamp timestamp;

    public Evidence() {
    }

    public Evidence(String id, String image, Timestamp timestamp) {
        this.id = id;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
