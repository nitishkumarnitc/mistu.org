package com.example.nitish.mistuorg.interests;

/**
 * Created by nitish on 26-07-2016.
 */
public class Interest {
    private String name;
    private int picId;
    private boolean isChecked;

    public Interest(String name, int picId) {
        this.name = name;
        this.picId = picId;
    }

    public Interest(String name, int picId, boolean isChecked) {
        this.name = name;
        this.picId = picId;
        this.isChecked = isChecked;
    }

    public Interest(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

