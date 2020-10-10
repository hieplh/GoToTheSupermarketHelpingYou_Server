package com.capstone.google.matrixobj;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ElementObject {

    @SerializedName("distance")
    private Map<String, String> distance;

    @SerializedName("duration")
    private Map<String, String> duration;

    @SerializedName("status")
    private String status;

    public Map<String, String> getDistance() {
        return distance;
    }

    public Map<String, String> getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }
}
