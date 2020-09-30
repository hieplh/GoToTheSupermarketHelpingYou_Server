/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Admin
 */
public class GsonHelper {

    public static String gson(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }
}
