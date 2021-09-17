package com.codewithshadow.linkedin_clone.models.story;

public class StoryModel {
    public String getStoryImg() {
        return storyImg;
    }

    public StoryModel(String storyImg, long timeStart, long timeEnd, String userId, String storyId, String timeUpload) {
        this.storyImg = storyImg;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.userId = userId;
        this.storyId = storyId;
        this.timeUpload = timeUpload;
    }


    public long getTimeStart() {
        return timeStart;
    }


    public String getStoryId() {
        return storyId;
    }


    public long getTimeEnd() {
        return timeEnd;
    }


    public String getTimeUpload() {
        return timeUpload;
    }


    private String storyImg;
    private long timeStart;


    private String storyId;


    public StoryModel() {

    }


    private String msg;

    private long timeEnd;
    private String userId;

    public String getUserId() {
        return userId;
    }


    private String timeUpload;

}
