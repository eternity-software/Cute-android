package ru.etysoft.cute.api.errors;

import org.json.JSONException;

public class ErrorHandler {

    String jsonResponse;
    JSONArrayList errorCodes;

    public ErrorHandler(String jsonResponse) throws JSONException {
        this.jsonResponse = jsonResponse;
        errorCodes = new JSONArrayList(jsonResponse);
    }

    public boolean isSessionExpired() {
        return errorCodes.has("Authorization unsuccessful");
    }

    public String getErrorCode() {
        return null;
    }

}
