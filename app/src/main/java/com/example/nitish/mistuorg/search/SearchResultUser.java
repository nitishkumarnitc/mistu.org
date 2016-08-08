package com.example.nitish.mistuorg.search;


public class SearchResultUser {
    private String name;
    private int hasUserConfirmed;
    private int userId;
    private String mobile;

    public SearchResultUser(int hasUserConfirmed, String name,int userId,String mobile) {
        this.hasUserConfirmed = hasUserConfirmed;
        this.name = name;
        this.userId=userId;
        this.mobile=mobile;
    }

    public String getMobile(){
        return this.mobile;
    }

    public void setMobile(String mobile){
        this.mobile=mobile;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHasUserConfirmed() {
        return this.hasUserConfirmed;
    }

    public void setHasUserConfirmed(int hasUserConfirmed) {
        this.hasUserConfirmed = hasUserConfirmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
