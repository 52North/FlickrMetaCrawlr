package org.meta.crawlr.server.error;

import javax.ws.rs.ext.Provider;

import com.aetrion.flickr.FlickrException;

@Provider
public class FlickrExceptionMapper extends AbstractExceptionMapper<FlickrException>  {

}
