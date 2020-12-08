package com.smhu.google.matrixobj;

import com.google.gson.annotations.SerializedName;
import com.smhu.helper.GsonHelper;
import com.smhu.url.UrlConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MatrixObject {

    @SerializedName("destination_addresses")
    private String[] destinationAddr;

    @SerializedName("origin_addresses")
    private String[] originAddr;

    @SerializedName("rows")
    private RowObject[] rows;

    @SerializedName("status")
    private String status;

    public String[] getDestinationAddr() {
        return destinationAddr;
    }

    public String[] getOriginAddr() {
        return originAddr;
    }

    public RowObject[] getRows() {
        return rows;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "DistanceMatrixObject{" + "destinationAddr=" + destinationAddr + ", originAddr=" + originAddr + ", rows=" + rows + ", status=" + status + '}';
    }
}
