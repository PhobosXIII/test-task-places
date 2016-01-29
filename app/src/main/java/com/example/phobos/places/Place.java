package com.example.phobos.places;

import java.util.Date;

public class Place {
    private Double latitude;
    private Double longitude;
    private String text;
    private String image;
    private Date lastVisited;

    public Place(Double latitude, Double longitude, String text, String image, Date lastVisited) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.text = text;
        this.image = image;
        this.lastVisited = lastVisited;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Date lastVisited) {
        this.lastVisited = lastVisited;
    }

    @Override
    public String toString() {
        return getText();
    }
}
