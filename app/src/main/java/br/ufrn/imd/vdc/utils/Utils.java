package br.ufrn.imd.vdc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Cephas on 04/04/2017.
 */

public class Utils {

    private HashMap<String, String> readingsMap;

    public Utils() {
        readingsMap = new HashMap<>();
    }

    public void putReadingOnMap(String key, String value) {
        readingsMap.put(key, value);
    }

    public String getJsonStringFromMap() {
        Gson gson = new Gson();
        return gson.toJson(readingsMap);
    }

    public HashMap<String, String> getReadingsMap() {
        if (readingsMap.size() != 0) {
            return readingsMap;
        } else {
            return null;
        }
    }

    public HashMap<String, String> getMapFromJsonString(String readingString) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<HashMap<String, String>>() {
        }.getType();
        return gson.fromJson(readingString, typeOfHashMap);
    }

}
