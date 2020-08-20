package ru.etysoft.cute.requests;

public class APIRunnable implements Runnable {

    public String response = null;

    public String url = null;

    public boolean isSuccess = false;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public void run() {

    }
}


