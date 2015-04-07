package org.n52.flickr.crawlr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.n52.crawlr.core.SosUploadr;
import org.n52.crawlr.entities.Entry;
import org.n52.crawlr.flickr.SosFlickrUploadrImpl;


public class SosUploadrImplIT {

	@Test
	public void testUploadPhotos() {
		
		// create fake FlickrPhotos:
		
		Entry flickrPhoto = new Entry();
		flickrPhoto.setAccuracy(0);
		flickrPhoto.setDatePosted(new Date());
		flickrPhoto.setDateTaken(new Date());
		flickrPhoto.setDescription("my photo decription");
		flickrPhoto.setID("my-photo-ID");
		flickrPhoto.setLatitude(52);
		flickrPhoto.setLongitude(7);
		
		Collection<String> photoTags = new ArrayList<String>();
		photoTags.add("my Tag");
		flickrPhoto.setTags(photoTags);
		
		flickrPhoto.setTitle("my photo title");
		flickrPhoto.setValue("http://myPhotoUrl");
		flickrPhoto.setUserId("my-user-ID");
		flickrPhoto.setUserName("my user name");
		
		List<Entry> photoList = new ArrayList<Entry>();
		photoList.add(flickrPhoto);
		
		// start testing the uploading:
		SosUploadr sosUploadr = new SosFlickrUploadrImpl();
		try {
			
			sosUploadr.uploadEntries(photoList, new ArrayList<String>(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	@Test
	public void testUploadPhotosNull() {
		
		// create fake FlickrPhotos:
		
		Entry flickrPhoto = new Entry();
		
		List<Entry> photoList = new ArrayList<Entry>();
		photoList.add(flickrPhoto);
		
		// start testing the uploading:
		SosUploadr sosUploadr = new SosFlickrUploadrImpl();
		try {
			
			sosUploadr.uploadEntries(photoList, new ArrayList<String>(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
