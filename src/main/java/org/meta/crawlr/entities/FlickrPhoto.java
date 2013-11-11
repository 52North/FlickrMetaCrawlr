package org.meta.crawlr.entities;

import java.util.Collection;
import java.util.Date;

public class FlickrPhoto {

	private String photoID;
	private String photoTitle;
	private String photoDescription;
	private String photoURL;
	private Collection<String> photoTags;
	private Date photoDateTaken;
	private Date photoDatePosted;
	private float photoLongitude;
	private float photoLatitude;
	private int geoAccuracy;
	private String userId;
	private String userName;

	public FlickrPhoto() {

	}

	@Override
	public String toString() {
		return "[" + userName + "'s photo: " + photoURL + ", " + photoTitle + "]";
	}

	public String getPhotoID() {
		return photoID;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public String getPhotoTitle() {
		return photoTitle;
	}

	public void setPhotoTitle(String photoTitle) {
		this.photoTitle = photoTitle;
	}

	public String getPhotoDescription() {
		return photoDescription;
	}

	public void setPhotoDescription(String photoDescription) {
		this.photoDescription = photoDescription;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public Collection<String> getPhotoTags() {
		return photoTags;
	}

	public void setPhotoTags(Collection<String> photoTags) {
		this.photoTags = photoTags;
	}

	public Date getPhotoDateTaken() {
		return photoDateTaken;
	}

	public void setPhotoDateTaken(Date photoDateTaken) {
		this.photoDateTaken = photoDateTaken;
	}

	public Date getPhotoDatePosted() {
		return photoDatePosted;
	}

	public void setPhotoDatePosted(Date photoDatePosted) {
		this.photoDatePosted = photoDatePosted;
	}

	public float getPhotoLongitude() {
		return photoLongitude;
	}

	public void setPhotoLongitude(float photoLongitude) {
		this.photoLongitude = photoLongitude;
	}

	public float getPhotoLatitude() {
		return photoLatitude;
	}

	public void setPhotoLatitude(float photoLatitude) {
		this.photoLatitude = photoLatitude;
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

}
