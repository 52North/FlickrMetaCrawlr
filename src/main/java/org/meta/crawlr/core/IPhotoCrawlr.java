package org.meta.crawlr.core;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.meta.crawlr.entities.FlickrPhoto;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

/**
 * @author <a href='mailto:broering@52north.org'>Arne Broering</a>
 */
public interface IPhotoCrawlr {

	public List<FlickrPhoto> crawlForPhotos(
			String minLongitude, String minLatitude, String maxLongitude, String maxLatitude,
			Date minTakenDate, Date maxTakenDate,
			String[] keywords)
					throws IOException, SAXException, FlickrException;
}
