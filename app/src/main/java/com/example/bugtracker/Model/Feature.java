package com.example.bugtracker.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Feature {
    private String featureCategory;
    private String featureDescription;
    private String featureId;
    private String imageUrl;
    private String publisher;
    private ArrayList<String > bugIds;
    private HashMap<String, Object> stepsDescription;

    public Feature() {
    }

    public Feature(String featureCategory, String featureDescription, String featureId, String imageUrl, String publisher, HashMap<String, Object> stepsDescription, ArrayList<String > bugIds) {
        this.featureCategory = featureCategory;
        this.featureDescription = featureDescription;
        this.featureId = featureId;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.bugIds = bugIds;
        this.bugIds.addAll(bugIds);
        this.stepsDescription.putAll(stepsDescription);

    }

    public String getFeatureCategory() {
        return featureCategory;
    }

    public void setFeatureCategory(String featureCategory) {
        this.featureCategory = featureCategory;
    }

    public String getFeatureDescription() {
        return featureDescription;
    }

    public void setFeatureDescription(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
    public ArrayList<String > getBugIds() {
        return bugIds;
    }

    public void setBugIds(ArrayList<String > bugIds) {
        this.bugIds = bugIds;
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
        return "Feature{" +
                "featureCategory='" + featureCategory + '\'' +
                ", featureDescription='" + featureDescription + '\'' +
                ", featureId='" + featureId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", publisher='" + publisher + '\'' +
                ", stepsDescription=" + stepsDescription +
                '}';
    }

    public void setStepsDescription(HashMap<String, Object> stepsDescription) {
        this.stepsDescription = stepsDescription;
    }
}
