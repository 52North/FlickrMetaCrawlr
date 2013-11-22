package org.meta.crawlr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.meta.crawlr.entities.FlickrPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.geo.GeoInterface;

public class PhotoCrawlrImpl implements IPhotoCrawlr {

	private static final Logger log = LoggerFactory.getLogger(PhotoCrawlrImpl.class);

	/**
	 * access to the Flickr API.
	 */
	private Flickr flickr;
	private String apiKey;
	private String secret;
	private String token;

	public PhotoCrawlrImpl() throws IOException, ParserConfigurationException {
		flickr = initFlickrConnector();
	}

	/**
	 * Helper method to initialize a {@link Flickr} object.
	 */
	private Flickr initFlickrConnector() throws IOException,
			ParserConfigurationException {
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("/setup.properties");
			Properties properties = new Properties();
			properties.load(in);
			apiKey = properties.getProperty("apiKey");
			secret = properties.getProperty("secret");
			token  = properties.getProperty("token");

			flickr = new Flickr(apiKey, secret, new REST());

			RequestContext requestContext = RequestContext.getRequestContext();
			Auth auth = new Auth();
			auth.setPermission(Permission.READ);
			auth.setToken(token);
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
	public List<FlickrPhoto> crawlForPhotos(
			double minLongitude, double minLatitude, double maxLongitude, double maxLatitude,
			Date minTakenDate, Date maxTakenDate,
			String[] keywords) throws IOException, SAXException, FlickrException {

		List<FlickrPhoto> flickrPhotos = new ArrayList<FlickrPhoto>();
		
		PhotosInterface photoInterface = flickr.getPhotosInterface();

		SearchParameters params = new SearchParameters();
		params.setTags(keywords);
		params.setTagMode("all");
		params.setHasGeo(true);
		params.setBBox(""+minLongitude, ""+minLatitude, ""+maxLongitude, ""+maxLatitude);
		params.setMinTakenDate(minTakenDate);
		params.setMaxTakenDate(maxTakenDate);

		int pageIndex = 1;
		int pages = 1;
		do {
			PhotoList photoList = photoInterface.search(params, 500, pageIndex);
			pages = photoList.getPages();

			log.info("number of photos on page " + pageIndex + ": "
					+ photoList.size() + " of " + photoList.getTotal()
					+ " photos for this bbox.");

			for (int i = 0; i < photoList.size(); i++) {
				Photo photo = (Photo) photoList.get(i);
				
				// careful, this call takes long:
				FlickrPhoto flickrPhoto = createFlickrPhoto(photo);
				log.info("Downloaded photo No. " + i + ".): " + flickrPhoto);
				
				flickrPhotos.add(flickrPhoto);
			}

			pageIndex++;

		} while (pageIndex < pages);

		return flickrPhotos;
	}
	
	/**
	 * Helper method to fill all data for a {@link FlickrPhoto}.
	 */
	@SuppressWarnings("unchecked")
	private FlickrPhoto createFlickrPhoto(Photo photo) throws IOException, SAXException, FlickrException {
		
		FlickrPhoto flickrPhoto = new FlickrPhoto();
		PhotosInterface photoInterface = flickr.getPhotosInterface();
		GeoInterface geoInterface = flickr.getGeoInterface();
		
		String photoID = photo.getId();
		flickrPhoto.setPhotoID(photoID);
        
        // photo info:
        Photo photoInfo = photoInterface.getInfo(photoID, secret);
        flickrPhoto.setPhotoTitle(photoInfo.getTitle());
        flickrPhoto.setPhotoURL(photoInfo.getUrl());
        flickrPhoto.setPhotoDescription(photoInfo.getDescription());
//        flickrPhoto.setPhotoTags(photoInfo.getTags());
//        flickrPhoto.setPhotoDescription(photoInfo.getDescription());
//        
//        if (photoInfo.getDatePosted() != null) {
//            flickrPhoto.setPhotoDatePosted(photoInfo.getDatePosted());
//        }
//        if (photoInfo.getDateTaken() != null) {
//            flickrPhoto.setPhotoDateTaken(photoInfo.getDateTaken());
//        }
//        
//        // geo:
//        GeoData photoGeo = geoInterface.getLocation(photoID);
//        if (photoGeo != null) {
//            flickrPhoto.setPhotoLongitude(photoGeo.getLongitude());
//            flickrPhoto.setPhotoLongitude(photoGeo.getLatitude());
//            flickrPhoto.setAccuracy(photoGeo.getAccuracy());
//        }
//        
//        // user:
//        User user = photoInfo.getOwner();
//        flickrPhoto.setUserId(user.getId());
//        flickrPhoto.setUserName(user.getUsername());
        
		return flickrPhoto;
	}

}
