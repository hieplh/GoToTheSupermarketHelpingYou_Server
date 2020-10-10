package com.capstone.google.matrixobj;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RowObject {

    @SerializedName("elements")
    private List<ElementObject> elements;

    public List<ElementObject> getElements() {
        return elements;
    }
}
