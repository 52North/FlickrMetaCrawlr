package org.meta.crawlr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

public class PhotoCrawlrImpl implements IPhotoCrawlr {

	private static Logger LOGGER = Logger.getLogger(PhotoCrawlrImpl.class
			.toString());

	/**
	 * access to the Flickr API.
	 */
	private Flickr flickr;

	public PhotoCrawlrImpl() throws IOException, ParserConfigurationException {
		flickr = initFlickrConnector();
	}

	private Flickr initFlickrConnector() throws IOException,
			ParserConfigurationException {
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("/setup.properties");
			Properties properties = new Properties();
			properties.load(in);

			flickr = new Flickr(properties.getProperty("apiKey"),
					properties.getProperty("secret"), new REST());

			RequestContext requestContext = RequestContext.getRequestContext();
			Auth auth = new Auth();
			auth.setPermission(Permission.READ);
			auth.setToken(properties.getProperty("token"));
			requestContext.setAuth(auth);

			Flickr.debugRequest = false;
			Flickr.debugStream = false;

			return flickr;

		} catch (IOException ioExc) {
			throw ioExc;
		} catch (ParserConfigurationException pcExc) {
			throw pcExc;
		} finally {
			in = null;
		}
	}

	@Override
	public String crawlForPhotos(
			String minLongitude, String minLatitude, String maxLongitude, String maxLatitude,
			Date minTakenDate, Date maxTakenDate,
			String[] keywords) throws IOException, SAXException, FlickrException {

		PhotosInterface photoInterface = flickr.getPhotosInterface();

		SearchParameters params = new SearchParameters();
		params.setTags(keywords);
		params.setTagMode("all");
		params.setHasGeo(true);
		params.setBBox(minLongitude, minLatitude, maxLongitude, maxLatitude);
		params.setMinTakenDate(minTakenDate);
		params.setMaxTakenDate(maxTakenDate);

		int pageIndex = 1;
		int pages = 1;
		do {
			PhotoList photoList = photoInterface.search(params, 500, pageIndex);
			pages = photoList.getPages();

			LOGGER.info("number of photos on page " + pageIndex + ": "
					+ photoList.size() + " of " + photoList.getTotal()
					+ " photos for this bbox.");

			for (int i = 0; i < photoList.size(); i++) {
				Photo photo = (Photo) photoList.get(i);
				
				// ... do stuff with Photo:
				photo.getGeoData(); 
				// ...
			}

			pageIndex++;

		} while (pageIndex < pages);

		return null;
	}

}
