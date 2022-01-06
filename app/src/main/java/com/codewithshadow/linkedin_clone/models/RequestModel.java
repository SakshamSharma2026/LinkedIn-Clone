package com.codewithshadow.linkedin_clone.models;

public class RequestModel {
    private String username;
    private String emailAddress;
    private String imageUrl;
    private String key;
    private String token;
    private String headline;

    public String getHeadline() {
        return headline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public RequestModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getKey() {
        return key;
    }

}
