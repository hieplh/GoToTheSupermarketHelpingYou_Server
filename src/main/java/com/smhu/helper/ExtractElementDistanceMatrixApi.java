package com.smhu.helper;

import com.smhu.google.matrixobj.DistanceMatrixObject;
import com.smhu.google.matrixobj.ElementObject;
import java.util.ArrayList;
import java.util.List;

public class ExtractElementDistanceMatrixApi {

    public List<ElementObject> getListElements(DistanceMatrixObject obj) {
        return obj.getRows()[0].getElements();
    }

    public List<String> getListDistance(List<ElementObject> list, String type) {
        List<String> listDistanceValues = new ArrayList<>();
        for (ElementObject element : list) {
            listDistanceValues.add(element.getDistance().get(type));
        }
        return listDistanceValues;
    }

    public List<String> getListDuration(List<ElementObject> list, String type) {
        List<String> listDurationValues = new ArrayList<>();
        for (ElementObject element : list) {
            listDurationValues.add(element.getDuration().get(type));
        }
        return listDurationValues;
    }
}
