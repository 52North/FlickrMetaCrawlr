package org.n52.crawlr.core;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.xml.parsers.ParserConfigurationException;

import org.n52.crawlr.flickr.FlickrCrawlrImpl;
import org.n52.crawlr.twitter.TwitterCrawlrImpl;

public class CrawlrFactory {
    
    public static ICrawlr getCrawlr(String source) throws IOException, ParserConfigurationException {
        if (Constants.TWITTER.equals(source)) {
               return new TwitterCrawlrImpl();
        } else if (Constants.FLICKR.equals(source)) { 
            return new FlickrCrawlrImpl();
        } else {
            throw new InvalidParameterException();
        }
    }
}
