package org.n52.flickr.crawlr.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.n52.crawlr.entities.Entry;
import org.n52.crawlr.flickr.FlickrCrawlrImpl;
import org.n52.crawlr.flickr.SosFlickrUploadrImpl;
import org.n52.oxf.valueDomains.time.TimePosition;

public class PhotoCrawlrImplIT {

	@Test
	public void testCrawlForPhotos() {

		// long/lat of Monterrey Bay:
		double targetLongitude = -121.901481;
		double targetLatitude = 36.618253;

		double minLongitude = targetLongitude - 20;
		double minLatitude = targetLatitude - 20;
		double maxLongitude = targetLongitude + 20;
		double maxLatitude = targetLatitude + 20;

		Date maxTakenDate = new Date(System.currentTimeMillis());
		Date minTakenDate = new TimePosition("2008-12-01").getCalendar().getTime();

		List<String> keywords = new ArrayList<String>();
		keywords.add("Monterrey");
		keywords.add("Airport");
		    

		try {
			List<Entry> photos = new FlickrCrawlrImpl().crawlForEntities(minLongitude, minLatitude, maxLongitude,
							maxLatitude, minTakenDate, maxTakenDate, keywords);
			
			for (Entry flickrPhoto : photos) {
				System.out.println(flickrPhoto.toString());
			}
			
			new SosFlickrUploadrImpl().uploadEntries(photos, keywords);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} 
		
	}


}
