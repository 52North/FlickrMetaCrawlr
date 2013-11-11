package org.meta.crawlr.core;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class PhotoCrawlrImplTest {

	@Test
	public void testCrawlForPhotos() {
		
		String minLongitude;
		String minLatitude;
		String maxLongitude;
		String maxLatitude;
		
		final Calendar now = GregorianCalendar.getInstance(); //(System.currentTimeMillis());
		Date maxTakenDate = now.getTime();
		now.roll(Calendar.DAY_OF_MONTH, -31);
		Date minTakenDate = now.getTime();
		
		String[] keywords = new String[] {"Beach"};
	}

}
