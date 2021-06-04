package ru.etysoft.cute.api.response;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.api.errors.ErrorHandler;

public class ResponseHandler {

    private String jsonResponse;
    private JSONObject response;

    public ResponseHandler(String jsonResponse) throws JSONException {
        this.jsonResponse = jsonResponse;
        response = new JSONObject(jsonResponse);
    }

    public boolean isSuccess() throws JSONException {
        boolean responseBoolean = false;

        if (response.getString("type").equals("success")) {
            responseBoolean = true;
        }
        return responseBoolean;
    }

    public ErrorHandler getErrorHandler() throws JSONException {
        return new ErrorHandler(jsonResponse);
    }

}
