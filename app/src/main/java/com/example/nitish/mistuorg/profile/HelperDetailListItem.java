package com.example.nitish.mistuorg.profile;

public class HelperDetailListItem {
    private int helperId;
    private String name;
    public HelperDetailListItem(int helperId,String name) {
        this.helperId = helperId;
        this.name = name;
    }

    public int getHelperId() {
        return helperId;
    }

    public void setHelperId(int helperId) {
        this.helperId = helperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
