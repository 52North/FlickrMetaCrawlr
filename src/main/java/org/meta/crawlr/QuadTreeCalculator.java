package org.meta.crawlr;
import java.io.File;
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
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.util.IOUtilities;
import com.csvreader.CsvWriter;

public class QuadTreeCalculator {

    static String apiKey;
    static String sharedSecret;
    Flickr f;
    RequestContext requestContext;
    Properties properties = null;
    static CsvWriter writer;
    static int rowCount = 0;
    
    // Run program for the following tags:
    private static String[] tags = new String[]{"New York", "Times Square"};

    /**
     * the constructor
     */
    public QuadTreeCalculator() throws ParserConfigurationException, IOException {
        Properties properties;
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }
        f = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());
        requestContext = RequestContext.getRequestContext();
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(properties.getProperty("token"));
        requestContext.setAuth(auth);
        Flickr.debugRequest = false;
        Flickr.debugStream = false;
        
        // prepare output file writer:
        String outputFileName = "c:/temp/quadTree-" + concatenate(tags) + ".csv";
        writer = new CsvWriter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), Charset.forName("UTF-8"))), '|');
        // write header:
        writer.writeRecord(new String[] { "bbox_id", "minX", "minY", "maxX", "maxY", "count" });
    }

    /**
     * Calculates a QuadTree consisting of multiple bboxes for the specified
     * overall bbox and a specified array of photo tags. Condition for a new
     * division of a square into 4 squares is that it contains > 5000 Flickr
     * photos.
     * 
     * The calculated bboxes of the QuadTree are stored in a file.
     */
    public void calculateQuadTree(double minX,
            double minY,
            double maxX,
            double maxY,
            String[] tags) throws IOException, SAXException, FlickrException
    {
        // send query for overall bbox to Flickr: 
        PhotosInterface photoInterface = f.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        params.setTags(tags);
        params.setTagMode("all");
        params.setHasGeo(true);
        params.setBBox("" + minX, "" + minY, "" + maxX, "" + maxY);
        PhotoList photoList = photoInterface.search(params, 500, 1);

        System.out.println("Overall bbox has " + photoList.getTotal() + " photos.");
        
        if (photoList.getTotal() > 4500) {

            // lower left:
            double minX_ll = minX;
            double minY_ll = minY;
            double maxX_ll = minX + ((maxX - minX) / 2);
            double maxY_ll = minY + ((maxY - minY) / 2);
            calculateQuadTree(minX_ll, minY_ll, maxX_ll, maxY_ll, tags);

            // lower right:
            double minX_lr = minX + ((maxX - minX) / 2);
            double minY_lr = minY;
            double maxX_lr = maxX;
            double maxY_lr = minY + ((maxY - minY) / 2);
            calculateQuadTree(minX_lr, minY_lr, maxX_lr, maxY_lr, tags);

            // upper left:
            double minX_ul = minX;
            double minY_ul = minY + ((maxY - minY) / 2);
            double maxX_ul = minX + ((maxX - minX) / 2);
            double maxY_ul = maxY;
            calculateQuadTree(minX_ul, minY_ul, maxX_ul, maxY_ul, tags);

            // upper right:
            double minX_ur = minX + ((maxX - minX) / 2);
            double minY_ur = minY + ((maxY - minY) / 2);
            double maxX_ur = maxX;
            double maxY_ur = maxY;
            calculateQuadTree(minX_ur, minY_ur, maxX_ur, maxY_ur, tags);
        } else {
            System.out.println(rowCount + " | " + minX + " | " + minY + " | " + maxX + " | " + maxY + " | " + photoList.getTotal());
            String[] dataRow = new String[] { "" + rowCount++, "" + minX, "" + minY, "" + maxX, "" + maxY, "" + photoList.getTotal() };
            if (photoList.getTotal() > 0) {
                writer.writeRecord(dataRow);
            }
        }
    }

    /** 
     * concatenates the specified String array to one String and separates by a "-" sign.
     */
    public static String concatenate(String[] sArray)
    {
        String result = "";
        for (int i = 0; i < sArray.length - 1; i++) {
            result += sArray[i] + "-";
        }
        result += sArray[sArray.length - 1];
        return result;
    }

    public static void main(String[] args) throws IOException, SAXException, FlickrException, ParserConfigurationException
    {
        if (args != null && args.length > 0) {
            tags = args;
        }
        
        new QuadTreeCalculator().calculateQuadTree(-180.0, -90.0, 180.0, 90.0, tags);
        writer.flush();
        writer.close();
    }
}
