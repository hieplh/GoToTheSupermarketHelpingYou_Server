package com.smhu.helper;

import com.smhu.google.geocoding.Geocoding;
import com.smhu.google.geocoding.Result;
import java.util.List;
import java.util.Map;

public class ExtractLocationGeocoding {

    public static List<Result> getResults(Geocoding geocoding) {
        return geocoding.getResults();
    }

    public static Map<String, String> getMapLocation(Result result) {
        return result.getGeometry().getLocation();
    }
}
