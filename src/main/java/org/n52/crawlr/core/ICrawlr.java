package org.n52.crawlr.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;


import org.n52.crawlr.entities.Entry;
import org.xml.sax.SAXException;

/**
 * @author <a href='mailto:broering@52north.org'>Arne Broering</a>
 */
public interface ICrawlr {

    String FLICKR_APIKEY = "flickr.apiKey";

    String FLICKR_SECRET = "flickr.secret";

    String FLICKR_TOKEN = "flickr.token";

    String TWITTER_CONSUMER_KEY = "twitter.consumerKey";

    String TWITTER_CONSUMER_SECRET = "twitter.consumerSecret";

    String TWITTER_TOKEN = "twitter.token";

    String TWITTER_TOKEN_SECRET = "twitter.tokenSecret";
    
    public abstract List<Entry> crawlForEntities(double minLongitude, double minLatitude, double maxLongitude,
            double maxLatitude, Date minDate, Date maxDate, Collection<String> keywords) throws IOException,
            SAXException, Exception;
}
