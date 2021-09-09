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

    public void setStoryImg(String storyImg) {
        this.storyImg = storyImg;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeUpload() {
        return timeUpload;
    }

    public void setTimeUpload(String timeUpload) {
        this.timeUpload = timeUpload;
    }

    private String storyImg;
    private long timeStart;


    private String storyId;


    public StoryModel() {

    }


    public StoryModel(String storyImg, String storyId, String msg) {
        this.storyImg = storyImg;
        this.storyId = storyId;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    private long timeEnd;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String timeUpload;

}
