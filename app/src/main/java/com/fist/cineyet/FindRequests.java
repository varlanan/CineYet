package com.fist.cineyet;

public class FindRequests {
    public String request_type;
    public FindRequests(){

    }
    public FindRequests(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
