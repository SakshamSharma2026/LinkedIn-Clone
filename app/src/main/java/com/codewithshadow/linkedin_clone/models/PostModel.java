package com.codewithshadow.linkedin_clone.models;

public class PostModel {
    private String imgUrl;
    private String description;
    private String key;
    private String username;
    private String user_profile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public PostModel() {

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

}
