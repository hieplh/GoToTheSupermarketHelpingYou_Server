package com.smhu.google.geocoding;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class Geometry {

    @SerializedName("location")
    private Map<String, String> location;

    @SerializedName("location_type")
    private String locationType;

    @SerializedName("viewport")
    private ViewPort viewport;

    public Map<String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public ViewPort getViewport() {
        return viewport;
    }

    public void setViewport(ViewPort viewport) {
        this.viewport = viewport;
    }

    @Override
    public String toString() {
        return "Geometry{" + "location=" + location + ", locationType=" + locationType + ", viewport=" + viewport + '}';
    }
}
