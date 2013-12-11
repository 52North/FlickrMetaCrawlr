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
import org.meta.crawlr.core.SosUploadrImpl;
import org.meta.crawlr.entities.FlickrPhoto;
import org.meta.crawlr.server.error.MissingParameterException;
import org.meta.crawlr.server.params.BoundingBoxParam;
import org.meta.crawlr.server.params.TimeParam;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

/**
 * Flickr Crawlr URL:
 * ==================
 * 
 * http://localhost:8080/flickrMetaCrawlr/search?keywords=Monterrey,Airport&bbox=-140,16,-100,56&minTime=2008-12-01&maxTime=2013-12-31
 * 
 * 
 * SOS URL:
 * ========
 * 
 * http://ows.dev.52north.org:8080/52n-wfs-webapp/sos/kvp?service=WFS&version=2.0.0&request=GetFeature&namespaces=xmlns(om%2Chttp%3A%2F%2Fwww.opengis.net%2Fom%2F2.0)&typenames=om%3AObservation&filter=%3CFilter%20xmlns%3D%22http%3A%2F%2Fwww.opengis.net%2Ffes%2F2.0%22%20xmlns%3Agml%3D%22http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2%22%20xmlns%3Axsi%3D%22http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema-instance%22%20xsi%3AschemaLocation%3D%22http%3A%2F%2Fwww.opengis.net%2Ffes%2F2.0%20http%3A%2F%2Fschemas.opengis.net%2Ffilter%2F2.0.0%2Ffilter.xsd%20http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2%20http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2.1%2Fbase%2Fgml.xsd%22%3E%3CDuring%3E%3CValueReference%3Eom%3AphenomenonTime%3C%2FValueReference%3E%3Cgml%3ATimePeriod%20gml%3Aid%3D%22tp_1%22%3E%3Cgml%3AbeginPosition%3E2008-12-01T14%3A00%3A00.000%2B01%3A00%3C%2Fgml%3AbeginPosition%3E%3Cgml%3AendPosition%3E2013-12-30T14%3A05%3A00.000%2B01%3A00%3C%2Fgml%3AendPosition%3E%3C%2Fgml%3ATimePeriod%3E%3C%2FDuring%3E%3C%2FFilter%3E
 * 
 * @author <a href="mailto:broering@52north.org>Arne Broering</a>
 */
@Path("/")
public class PhotoCrawlrResource  {
    
    public PhotoCrawlrResource() {
    }


    @GET
	@Path("/registerSensor")
    @Produces(MediaType.TEXT_HTML)
    public Response doRegisterSensor() throws IOException {
    	new SosUploadrImpl().registerProcedure();
		
		String output = "Procedure registered.";
		return Response.status(200).entity(output).build();
    }
    
    @GET
	@Path("/search")
    @Produces(MediaType.TEXT_HTML)
	public Response doSearch(
			@QueryParam("keywords") String keywords,
			@QueryParam("bbox") BoundingBoxParam bbox,
			@QueryParam("minTime") TimeParam minTime,
			@QueryParam("maxTime") TimeParam maxTime) throws IOException, SAXException, FlickrException, ParserConfigurationException, ExceptionReport, OXFException
    {
    	if(keywords == null){
    		throw new MissingParameterException("keywords");
    	}
    	
    	if(bbox == null){
    		throw new MissingParameterException("bbox");
    	}
    	double minLongitude = bbox.getMinLongitude();
    	double minLatitude  = bbox.getMinLatitude();
    	double maxLongitude = bbox.getMaxLongitude();
    	double maxLatitude  = bbox.getMaxLatitude();
    	
    	if(minTime == null){
    		throw new MissingParameterException("minTime");
    	}
    	Date minTakenDate = minTime.getTime().getCalendar().getTime();
    	
    	if(maxTime == null){
    		throw new MissingParameterException("maxTime");
    	}
    	Date maxTakenDate = maxTime.getTime().getCalendar().getTime();
    	
		List<FlickrPhoto> photoList = new PhotoCrawlrImpl().crawlForPhotos(minLongitude, minLatitude, maxLongitude, maxLatitude, minTakenDate, maxTakenDate, keywords.split(","));
		
		new SosUploadrImpl().uploadPhotos(photoList);
		
		String output = "<b>Downloaded the following photos from Flickr and uploaded them to the SOS:</b> <br>";
		for (FlickrPhoto flickrPhoto : photoList) {
			output += flickrPhoto + "<br>";
		}
		return Response.status(200).entity(output).build();
    	
	}
}
