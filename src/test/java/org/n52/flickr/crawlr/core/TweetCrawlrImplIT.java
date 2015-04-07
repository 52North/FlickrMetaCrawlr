package org.n52.flickr.crawlr.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.n52.crawlr.entities.Entry;
import org.n52.crawlr.twitter.TwitterCrawlrImpl;
import org.n52.oxf.valueDomains.time.TimePosition;

public class TweetCrawlrImplIT {

	@Test
	public void testCrawlForTweets(){ 
	
		double targetLongitude = 7.628694;
		double targetLatitude = 51.962944;

		double minLongitude = targetLongitude - 2;
		double minLatitude = targetLatitude - 2;
		double maxLongitude = targetLongitude + 2;
		double maxLatitude = targetLatitude + 2;

		Date maxTakenDate = new Date(System.currentTimeMillis());
		Date minTakenDate = new TimePosition("2013-11-01").getCalendar().getTime();

		List<String> keywords = new ArrayList<String>();
                keywords.add("Stadt");

		try {
			List<Entry> tweets = new TwitterCrawlrImpl().crawlForEntities(minLongitude, minLatitude, maxLongitude,
							maxLatitude, minTakenDate, maxTakenDate, keywords);
			
			for (Entry tweet : tweets) {
				System.out.println(tweet.toString());
			}
			
	//		new SosUploadrImpl().uploadPhotos(tweets);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} 
		
	}


}
