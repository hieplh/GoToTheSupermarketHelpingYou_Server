package com.smhu.google.geocoding;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class Result {

    @SerializedName("address_components")
    private List<AddressComponent> addressComponents;

    @SerializedName("formatted_address")
    private String formattedAddress;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("partial_match")
    private boolean partialMatch;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("plus_code")
    private Map<String, String> plusCode;

    @SerializedName("types")
    private String[] types;

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public boolean isPartialMatch() {
        return partialMatch;
    }

    public void setPartialMatch(boolean partialMatch) {
        this.partialMatch = partialMatch;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Map<String, String> getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(Map<String, String> plusCode) {
        this.plusCode = plusCode;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "Result{" + "addressComponents=" + addressComponents + ", formattedAddress=" + formattedAddress + ", geometry=" + geometry + ", partialMatch=" + partialMatch + ", placeId=" + placeId + ", plusCode=" + plusCode + ", types=" + types + '}';
    }
}
