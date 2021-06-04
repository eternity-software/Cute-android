package ru.etysoft.cute.api;

public class APIRunnable implements Runnable {

    private String response = null;
    private String url = null;


    @Override
    public void run() {

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


