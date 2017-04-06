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
        this.readingsMap = new HashMap<String, String>();
    }

    public void putReadingOnMap(String key, String value) {
        this.readingsMap.put(key, value);
    }

    public String getJsonStringFromMap() {
        Gson gson = new Gson();
        String json = gson.toJson(this.readingsMap);
        return json;
    }

    public HashMap<String, String> getReadingsMap() {
        if (readingsMap.size() != 0) {
            return this.readingsMap;
        } else {
            return null;
        }
    }

    public HashMap<String, String> getMapFromJsonString(String readingString) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> map = gson.fromJson(readingString, typeOfHashMap); // This type must match TypeToken
        return map;
    }

}
