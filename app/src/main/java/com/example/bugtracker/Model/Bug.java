package com.example.bugtracker.Model;


import java.util.HashMap;

public class Bug {
    private String bugCategory;
    private String bugDescription;
    private String bugId;
    private String imageUrl;
    private String publisher;
    private HashMap<String, Object> stepsDescription;

    public Bug() {
    }

    public Bug(String bugCategory, String bugDescription, String bugId, String imageUrl, String publisher, HashMap<String, Object> stepsDescription) {
        this.bugCategory = bugCategory;
        this.bugDescription = bugDescription;
        this.bugId = bugId;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.stepsDescription.putAll(stepsDescription);
    }

    public String getBugCategory() {
        return bugCategory;
    }

    public void setBugCategory(String bugCategory) {
        this.bugCategory = bugCategory;
    }

    public String getBugDescription() {
        return bugDescription;
    }

    public void setBugDescription(String bugDescription) {
        this.bugDescription = bugDescription;
    }

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public HashMap<String, Object> getStepsDescription() {
        return stepsDescription;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "BugCategory='" + bugCategory + '\'' +
                ", BugDescription='" + bugDescription + '\'' +
                ", BugId='" + bugId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", publisher='" + publisher + '\'' +
                ", stepsDescription=" + stepsDescription +
                '}';
    }

    public void setStepsDescription(HashMap<String, Object> stepsDescription) {
        this.stepsDescription = stepsDescription;
    }
}
