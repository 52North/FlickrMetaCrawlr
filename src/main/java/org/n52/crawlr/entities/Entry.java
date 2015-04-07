package org.n52.crawlr.entities;

import java.util.Collection;
import java.util.Date;

import org.n52.crawlr.core.Constants;

public class Entry {

    private String id;

    private String title;

    private StringBuilder description = new StringBuilder();

    private String value;

    private Collection<String> tags;

    private Date dateTaken;

    private Date datePosted;

    private double longitude;

    private double latitude;

    private int geoAccuracy;

    private String userId;

    private String userName;

    public Entry() {

    }

    @Override
    public String toString() {

        String lineSeparator = System.getProperty(Constants.LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        builder.append(lineSeparator);
        builder.append(getUserName()).append(":").append(lineSeparator);
        builder.append(getID()).append(lineSeparator);
        builder.append(getTitle()).append(lineSeparator);
        builder.append(getDatePosted()).append("/").append(getDateTaken()).append(lineSeparator);
        builder.append(getLatitude()).append("/").append(getLongitude()).append(lineSeparator);
        builder.append(getValue()).append(lineSeparator);
        builder.append(getDescription()).append(lineSeparator);

        return builder.toString();
        // return "[" + userName + "'s photo: " +url + ", " + title + "]";

    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description.toString();
    }
    
    public void setDescription(String description) {
       addToDescription(description);
    }
    
    public void addToDescription(String description) {
        if (this.description.length() >  0) {
            this.description.append(", ");
        }
        this.description.append(description);
    }
    
    public void addToDescription(Collection<String> descriptions) {
        for (String description : descriptions) {
            addToDescription(description);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double d) {
        this.longitude = d;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAccuracy() {
        return geoAccuracy;
    }

    public void setAccuracy(int geoAccuracy) {
        this.geoAccuracy = geoAccuracy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSetTags() {
        return getTags() != null && !getTags().isEmpty();
    }

}
