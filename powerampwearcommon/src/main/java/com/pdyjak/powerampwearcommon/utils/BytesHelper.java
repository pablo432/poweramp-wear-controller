package com.pdyjak.powerampwearcommon.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class BytesHelper {
    public static byte[] toBytes(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        try {
            return json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Should never happen");
            return null;
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> classOfT) {
        Gson gson = new Gson();
        try {
            String json = new String(bytes, "UTF-8");
            return gson.fromJson(json, classOfT);
        } catch (UnsupportedEncodingException | JsonSyntaxException e) {
            System.out.println("Should never happen");
            return null;
        }
    }
}
