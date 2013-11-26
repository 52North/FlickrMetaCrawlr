/*
 * Copyright (C) 2013 The enviroCar project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meta.crawlr.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.meta.crawlr.core.PhotoCrawlrImpl;
import org.meta.crawlr.entities.FlickrPhoto;
import org.meta.crawlr.server.params.BoundingBoxParam;
import org.meta.crawlr.server.params.TimeParam;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

/**
 * http://localhost:8080/flickrMetaCrawlr/search?keywords=Monterrey,Airport&bbox=123,456,789,135&minTime=2008-12-01&maxTime=2009-12-31
 * @author Arne
 *
 */
@Path("/")
public class PhotoCrawlrResource  {
    
    public PhotoCrawlrResource() {
    }

    @GET
	@Path("/search")
    @Produces(MediaType.TEXT_HTML)
	public Response doSearch(
			@QueryParam("keywords") String keywords,
			@QueryParam("bbox") BoundingBoxParam bbox,
			@QueryParam("minTime") TimeParam minTime,
			@QueryParam("maxTime") TimeParam maxTime) throws IOException, SAXException, FlickrException, ParserConfigurationException
    {
    	double minLongitude = bbox.getMinLongitude();
    	double minLatitude = bbox.getMinLatitude();
    	double maxLongitude = bbox.getMaxLongitude();
    	double maxLatitude = bbox.getMaxLatitude();
    	
    	Date minTakenDate = minTime.getTime().getCalendar().getTime();
    	Date maxTakenDate = maxTime.getTime().getCalendar().getTime();
    	
		List<FlickrPhoto> photoList = new PhotoCrawlrImpl().crawlForPhotos(minLongitude, minLatitude, maxLongitude, maxLatitude, minTakenDate, maxTakenDate, keywords.split(","));
		
		String output = "";
		for (FlickrPhoto flickrPhoto : photoList) {
			output += flickrPhoto + "<br>";
		}
		return Response.status(200).entity(output).build();
    	
	}
}
