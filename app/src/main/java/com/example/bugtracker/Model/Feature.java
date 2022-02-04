package com.example.bugtracker.Model;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

public class Feature {
    private String featureCategory;
    private String featureDescription;
    private String featureId;
    private String imageUrl;
    private String publisher;
    private HashMap<String, Object> stepsDescription;

    public Feature() {
    }

    public Feature(String featureCategory, String featureDescription, String featureId, String imageUrl, String publisher, HashMap<String, Object> stepsDescription) {
        this.featureCategory = featureCategory;
        this.featureDescription = featureDescription;
        this.featureId = featureId;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
//        for (int i = 0;i<stepsDescription.length; i++) {
//            this.stepsDescription[i] = stepsDescription[i];
//        }
//        for (int i = 0;i<stepsDescription.size(); i++) {
            this.stepsDescription.putAll(stepsDescription);
//        }

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

    public void setStepsDescription(HashMap<String, Object> stepsDescription) {
        this.stepsDescription = stepsDescription;
    }
}
