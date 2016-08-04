package com.example.nitish.mistuorg.notification;

/**
 * Created by nitish on 02-08-2016.
 */
public class NotiContactReqListItem {
    private String name;
    private float rating;
    private int requesterID;
    private int type;//whether accept or request 0 for request and 1 for accept
    private String mobile;
    private int hasUserConfirmed;

    public NotiContactReqListItem(String name, float rating, int requesterID, int type, String mobile, int hasUserConfirmed) {
        this.name = name;
        this.rating = rating;
        this.requesterID = requesterID;
        this.type = type;
        this.mobile = mobile;
        this.hasUserConfirmed = hasUserConfirmed;
    }

    public int getHasUserConfirmed() {
        return hasUserConfirmed;
    }

    public void setHasUserConfirmed(int hasUserConfirmed) {
        this.hasUserConfirmed = hasUserConfirmed;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(int requesterID) {
        this.requesterID = requesterID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
