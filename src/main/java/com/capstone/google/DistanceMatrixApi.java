package com.capstone.google;

import com.google.maps.GeoApiContext;

public class DistanceMatrixApi {

    GeoApiContext context;

    public DistanceMatrixApi() {
        context = new GeoApiContext.Builder()
                .apiKey("")
                .build();
    }

    
}
