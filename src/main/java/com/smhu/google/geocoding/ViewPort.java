package com.smhu.google.geocoding;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ViewPort {

    @SerializedName("northeast")
    private Map<String, String> northeast;

    @SerializedName("southwest")
    private Map<String, String> southwest;

    public Map<String, String> getNortheast() {
        return northeast;
    }

    public void setNortheast(Map<String, String> northeast) {
        this.northeast = northeast;
    }

    public Map<String, String> getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Map<String, String> southwest) {
        this.southwest = southwest;
    }

    @Override
    public String toString() {
        return "ViewPort{" + "northeast=" + northeast + ", southwest=" + southwest + '}';
    }

}
