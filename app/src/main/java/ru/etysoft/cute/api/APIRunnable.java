package ru.etysoft.cute.api;

public class APIRunnable implements Runnable {

    private String response = null;
    private String url = null;
    private String errorCode;
    private boolean isSuccess = false;

    @Override
    public void run() {

    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String code) {
        this.errorCode = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String response) {
        this.url = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}


