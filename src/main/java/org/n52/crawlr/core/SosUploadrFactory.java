package org.n52.crawlr.core;

import java.security.InvalidParameterException;

import org.n52.crawlr.flickr.SosFlickrUploadrImpl;
import org.n52.crawlr.twitter.SosTwitterUploadrImpl;

public class SosUploadrFactory {

    public static SosUploadr getSosUploadrImpl(String source) {
        if (Constants.TWITTER.equals(source)) {
               return new SosTwitterUploadrImpl();
        } else if (Constants.FLICKR.equals(source)) { 
            return new SosFlickrUploadrImpl();
        } else {
            throw new InvalidParameterException();
        }
    }
    
}
