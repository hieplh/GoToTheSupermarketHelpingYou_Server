package com.smhu.google.geocoding;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Geocoding {

    @SerializedName("results")
    private List<Result> results;

    @SerializedName("status")
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Geocoding{" + "results=" + results + ", status=" + status + '}';
    }
}
