package com.capstone.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
