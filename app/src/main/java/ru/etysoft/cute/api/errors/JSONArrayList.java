package ru.etysoft.cute.api.errors;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayList extends JSONArray {

    public JSONArrayList(String json) throws JSONException {
        super(json);
    }

    public boolean has(String s) {
        JSONArray jsonArray = this;
        boolean result = false;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(s)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
