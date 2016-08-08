package com.example.nitish.mistuorg.profile;


public class ProfileListItem {
    private int helpId,userId;
    private String title;
    private String description;
    private String category;
    private String tag1,tag2,tag3;
    private String name;

    public ProfileListItem(String category, String description, String name,
                           int helpId,String tag1,
                           String tag2, String tag3, String title, int userId) {

        this.category = category;
        this.description = description;
        this.name = name;
        this.helpId = helpId;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.title = title;
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHelpId() {
        return helpId;
    }

    public void setHelpId(int helpId) {
        this.helpId = helpId;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
