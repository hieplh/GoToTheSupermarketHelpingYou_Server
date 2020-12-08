package com.smhu.google.geocoding;

import com.google.gson.annotations.SerializedName;

public class AddressComponent {

    @SerializedName("long_name")
    private String longName;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("types")
    private String[] types;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "AddressComponents{" + "longName=" + longName + ", shortName=" + shortName + ", types=" + types + '}';
    }
    
}
