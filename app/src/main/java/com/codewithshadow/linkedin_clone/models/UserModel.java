package com.codewithshadow.linkedin_clone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String username;
    private String emailAddress;
    private String imageUrl;
    private String key;
    private String token;
    private String headline;

    protected UserModel(Parcel in) {
        username = in.readString();
        emailAddress = in.readString();
        imageUrl = in.readString();
        key = in.readString();
        token = in.readString();
        headline = in.readString();
        location = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

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

    public UserModel(String username, String emailAddress, String imageUrl, String key, String token, String location, String headline, String about) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.imageUrl = imageUrl;
        this.key = key;
        this.token = token;
        this.location = location;
        this.headline = headline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(emailAddress);
        dest.writeString(imageUrl);
        dest.writeString(key);
        dest.writeString(token);
        dest.writeString(headline);
        dest.writeString(location);
    }
}
