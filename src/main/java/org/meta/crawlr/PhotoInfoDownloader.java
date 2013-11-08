package org.meta.crawlr;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.contacts.ContactsInterface;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.geo.GeoInterface;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.aetrion.flickr.util.IOUtilities;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


/**
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 *
 */
public class PhotoInfoDownloader {

    Flickr f;
    REST rest;
    RequestContext requestContext;
    Properties properties = null;
    CsvWriter writer;
    
    // Run program for the following tags:
    public static String[] tags = new String[]{"New York", "Time Square"};
    
    
    
    public PhotoInfoDownloader() throws ParserConfigurationException, IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }
        f = new Flickr(
            properties.getProperty("apiKey"),
            properties.getProperty("secret"),
            new REST()
        );
        requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(properties.getProperty("token"));
        requestContext.setAuth(auth);
        Flickr.debugRequest = false;
        Flickr.debugStream  = false;
    }

    public void downloadStats() throws IOException, SAXException, FlickrException, InterruptedException {
        
        GeoInterface geoInterface = f.getGeoInterface();
        PeopleInterface pplInterface = f.getPeopleInterface();
        ContactsInterface contactsInterface = f.getContactsInterface();
        PhotosetsInterface photosetsInterface = f.getPhotosetsInterface();
        PhotosInterface photoInterface = f.getPhotosInterface();
        
        String inputFileName = "c:/temp/quadTree-" + QuadTreeCalculator.concatenate(tags) + ".csv";
        CsvReader reader = new CsvReader(inputFileName);
        reader.setDelimiter('|');
        
        // read header from input file:
        reader.readRecord();
        
        // read data rows from input file:
        while (reader.readRecord()) {
            String bboxID = reader.get(0);
            
            
            System.out.println("downloading bbox '" + bboxID + "'");
            
            //// ------------------------ prepare output file writer:
            String outputFileName = "c:/temp/flickrPhotoData-" + QuadTreeCalculator.concatenate(tags) + "-" + bboxID + ".csv";
            writer = new CsvWriter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFileName),Charset.forName("UTF-8"))), '|');
            
            // write header to output file:
            String[] headerRow = new String[] {
                    "photoID", "photoTitle", "photoUrl", "photoAccuracy", "photoTags", "photoTagsCount", 
                    "photoCommentsCount", "photoDatePosted", "photoDateTaken",
                    "photoDescription", "photoNotes", "photoNotesCount", "photoMedia", "photoMediaStatus", "photoLicense",
                    "photoIsFamilyFlag", "photoIsFriendFlag", "photoIsPrimary",
                    "photoLongitude", "photoLatitude", 
                    "userID", "userName", "userRealName", "userPhotoCount", "userPhotoFirstDate", "userPhotoFirstDateTaken",
                    "userLocation", "userBuddyIconUrl", "userIsPro", "userIsAdmin",
                    "userContactsCount", "userPhotoSetsCount"};
            writer.writeRecord(headerRow);
            
            String minX = reader.get(1);
            String minY = reader.get(2);
            String maxX = reader.get(3);
            String maxY = reader.get(4);
            
            SearchParameters params = new SearchParameters();
            params.setTags(tags);
            params.setTagMode("all");
            params.setHasGeo(true);
            params.setBBox(minX, minY, maxX, maxY);
            
            FlickrQuerier fq = new FlickrQuerier(this, photoInterface, geoInterface, pplInterface, contactsInterface, photosetsInterface);
            
            int pageIndex=1;
            int pages=1;
            do {
                PhotoList photoList = photoInterface.search(params, 500, pageIndex);
                pages = photoList.getPages();
                
                System.out.println("number of photos on page " + pageIndex +": " + photoList.size() + " of "+ photoList.getTotal() + " photos for this bbox.");
            
                for (int i=0; i < photoList.size(); i++) {
                    Photo photo = (Photo) photoList.get(i);
                    fq.downloadPhoto(i, photo);
                }
                
                pageIndex++;
                
            } while (pageIndex<pages);
            
            // finish up the output file writing by 'flushing' and 'closing':
            writer.flush();
            writer.close();
        }
        
        System.out.println("program finished");
    }
    
    
    /**
     * 
     * @param dataRow
     * @throws IOException
     */
    public synchronized void writeToFile (String[] dataRow) throws IOException {
        writer.writeRecord(dataRow);
    }
    
    /**
     * 
     * @param tg
     * @throws InterruptedException
     */
    void join( ThreadGroup tg ) throws InterruptedException
    {
      synchronized( tg )
      {
        while ( tg.activeCount() > 0 )
          tg.wait( 10 );
      }
    }
    
    public static void main(String[] args) throws IOException, SAXException, FlickrException, ParserConfigurationException, InterruptedException
    {
        if (args != null && args.length > 0) {
            tags = args;
        }
        new PhotoInfoDownloader().downloadStats();
    }
}
