package com.codewithshadow.linkedin_clone.models.user;

public class UserModel {
    private String username;
    private String emailAddress;
    private String imageUrl;
    private String key;
    private String token;
    private String headline;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public UserModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public UserModel(String username, String emailAddress, String imageUrl, String key, String token, String location, String headline) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.imageUrl = imageUrl;
        this.key = key;
        this.token = token;
        this.location = location;
        this.headline = headline;
    }
}
