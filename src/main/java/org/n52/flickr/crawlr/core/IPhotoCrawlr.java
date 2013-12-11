package org.n52.flickr.crawlr.core;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.n52.flickr.crawlr.entities.FlickrPhoto;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

/**
 * @author <a href='mailto:broering@52north.org'>Arne Broering</a>
 */
public interface IPhotoCrawlr {

	public List<FlickrPhoto> crawlForPhotos(
			double minLongitude, double minLatitude, double maxLongitude, double maxLatitude,
			Date minTakenDate, Date maxTakenDate,
			String[] keywords)
					throws IOException, SAXException, FlickrException;
}
