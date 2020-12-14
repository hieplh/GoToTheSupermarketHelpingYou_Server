package com.smhu.helper;

import com.smhu.google.matrixobj.MatrixObject;
import com.smhu.url.UrlConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MatrixObjBuilder {

    public static MatrixObject getMatrixObject(String source, List<String> destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }

    public static MatrixObject getMatrixObject(String[] source, String destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }

    public static MatrixObject getMatrixObject(String[] source, List<String> destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }
}
