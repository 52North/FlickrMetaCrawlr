package org.n52.crawlr.flickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.n52.crawlr.core.ICrawlr;
import org.n52.crawlr.core.PropertiesLoader;
import org.n52.crawlr.entities.Entry;
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

public class FlickrCrawlrImpl implements ICrawlr {

    private static final Logger log = LoggerFactory.getLogger(FlickrCrawlrImpl.class);

    /**
     * access to the Flickr API.
     */
    private Flickr flickr;

    private String apiKey;

    private String secret;

    private String token;

    public FlickrCrawlrImpl() throws IOException, ParserConfigurationException {
        flickr = initFlickrConnector();
    }

    /**
     * Helper method to initialize a {@link Flickr} object.
     */
    private Flickr initFlickrConnector() throws IOException, ParserConfigurationException {
        Properties properties = PropertiesLoader.getInstance().getProperties();
        apiKey = properties.getProperty(FLICKR_APIKEY);
        secret = properties.getProperty(FLICKR_SECRET);
        token = properties.getProperty(FLICKR_TOKEN);

        flickr = new Flickr(apiKey, secret, new REST());

        RequestContext requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(token);
        requestContext.setAuth(auth);

        Flickr.debugRequest = false;
        Flickr.debugStream = false;

        return flickr;
    }

    @Override
    public List<Entry> crawlForEntities(double minLongitude, double minLatitude, double maxLongitude,
            double maxLatitude, Date minDate, Date maxDate, Collection<String> keywords) throws IOException,
            SAXException, FlickrException {

        List<Entry> flickrPhotos = new ArrayList<Entry>();

        PhotosInterface photoInterface = flickr.getPhotosInterface();

        SearchParameters params = new SearchParameters();
        params.setTags(keywords.toArray(new String[0]));
        params.setTagMode("all");
        params.setHasGeo(true);
        params.setBBox("" + minLongitude, "" + minLatitude, "" + maxLongitude, "" + maxLatitude);
        params.setMinTakenDate(minDate);
        params.setMaxTakenDate(maxDate);

        int pageIndex = 1;
        int pages = 1;
        do {
            PhotoList photoList = photoInterface.search(params, 500, pageIndex);
            pages = photoList.getPages();

            log.info("number of photos on page " + pageIndex + ": " + photoList.size() + " of " + photoList.getTotal()
                    + " photos for this bbox.");

            for (int i = 0; i < photoList.size(); i++) {
                Photo photo = (Photo) photoList.get(i);

                // careful, this call takes long:
                Entry flickrPhoto = createFlickrPhoto(photo);
                log.info("Downloaded photo No. " + i + ".): " + flickrPhoto);

                flickrPhotos.add(flickrPhoto);
            }

            pageIndex++;

        } while (pageIndex < pages);

        return flickrPhotos;
    }

    /**
     * Helper method to fill all data for a {@link Entry}.
     */
    @SuppressWarnings("unchecked")
    private Entry createFlickrPhoto(Photo photo) throws IOException, SAXException, FlickrException {

        Entry flickrPhoto = new Entry();
        PhotosInterface photoInterface = flickr.getPhotosInterface();
        GeoInterface geoInterface = flickr.getGeoInterface();

        String photoID = photo.getId();
        flickrPhoto.setID(photoID);

        // photo info:
        Photo photoInfo = photoInterface.getInfo(photoID, secret);
        flickrPhoto.setTitle(photoInfo.getTitle());
        flickrPhoto.setValue(photoInfo.getUrl());
        flickrPhoto.setDescription(photoInfo.getDescription());
        flickrPhoto.setTags(photoInfo.getTags());
        flickrPhoto.setDescription(photoInfo.getDescription());

        if (photoInfo.getDatePosted() != null) {
            flickrPhoto.setDatePosted(photoInfo.getDatePosted());
        }
        if (photoInfo.getDateTaken() != null) {
            flickrPhoto.setDateTaken(photoInfo.getDateTaken());
        }

        // geo:
        GeoData photoGeo = geoInterface.getLocation(photoID);
        if (photoGeo != null) {
            flickrPhoto.setLongitude(photoGeo.getLongitude());
            flickrPhoto.setLatitude(photoGeo.getLatitude());
            flickrPhoto.setAccuracy(photoGeo.getAccuracy());
        }

        // user:
        User user = photoInfo.getOwner();
        flickrPhoto.setUserId(user.getId());
        flickrPhoto.setUserName(user.getUsername());

        return flickrPhoto;
    }

}
