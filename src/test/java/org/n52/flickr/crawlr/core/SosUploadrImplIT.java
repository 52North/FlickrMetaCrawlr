package org.n52.flickr.crawlr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.n52.flickr.crawlr.core.SosUploadrImpl;
import org.n52.flickr.crawlr.entities.FlickrPhoto;


public class SosUploadrImplIT {

	@Test
	public void testUploadPhotos() {
		
		// create fake FlickrPhotos:
		
		FlickrPhoto flickrPhoto = new FlickrPhoto();
		flickrPhoto.setAccuracy(0);
		flickrPhoto.setPhotoDatePosted(new Date());
		flickrPhoto.setPhotoDateTaken(new Date());
		flickrPhoto.setPhotoDescription("my photo decription");
		flickrPhoto.setPhotoID("my-photo-ID");
		flickrPhoto.setPhotoLatitude(52);
		flickrPhoto.setPhotoLongitude(7);
		
		Collection<String> photoTags = new ArrayList<String>();
		photoTags.add("my Tag");
		flickrPhoto.setPhotoTags(photoTags);
		
		flickrPhoto.setPhotoTitle("my photo title");
		flickrPhoto.setPhotoURL("http://myPhotoUrl");
		flickrPhoto.setUserId("my-user-ID");
		flickrPhoto.setUserName("my user name");
		
		List<FlickrPhoto> photoList = new ArrayList<FlickrPhoto>();
		photoList.add(flickrPhoto);
		
		// start testing the uploading:
		SosUploadrImpl sosUploadr = new SosUploadrImpl();
		try {
			
			sosUploadr.uploadPhotos(photoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	@Test
	public void testUploadPhotosNull() {
		
		// create fake FlickrPhotos:
		
		FlickrPhoto flickrPhoto = new FlickrPhoto();
		
		List<FlickrPhoto> photoList = new ArrayList<FlickrPhoto>();
		photoList.add(flickrPhoto);
		
		// start testing the uploading:
		SosUploadrImpl sosUploadr = new SosUploadrImpl();
		try {
			
			sosUploadr.uploadPhotos(photoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
