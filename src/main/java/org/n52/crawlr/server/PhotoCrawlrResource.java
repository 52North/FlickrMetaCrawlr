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
package org.n52.crawlr.server;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.n52.crawlr.core.Constants;
import org.n52.crawlr.core.CrawlrFactory;
import org.n52.crawlr.core.ICrawlr;
import org.n52.crawlr.core.SosUploadr;
import org.n52.crawlr.core.SosUploadrFactory;
import org.n52.crawlr.entities.Entry;
import org.n52.crawlr.flickr.FlickrCrawlrImpl;
import org.n52.crawlr.flickr.SosFlickrUploadrImpl;
import org.n52.crawlr.server.error.MissingParameterException;
import org.n52.crawlr.server.params.BoundingBoxParam;
import org.n52.crawlr.server.params.TimeParam;
import org.n52.crawlr.twitter.SosTwitterUploadrImpl;
import org.n52.crawlr.twitter.TwitterCrawlrImpl;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import twitter4j.TwitterException;

import com.aetrion.flickr.FlickrException;

/**
 * Flickr Crawlr URL: ==================
 * 
 * http://localhost:8080/socialMediaMetaCrawlr-0.0.1-SNAPSHOT/search?source=flickr&keywords=Monterrey,Airport&bbox=-140,16,-100,56&minTime=2008-12-01&maxTime=2013-12-31
 * 
 * Twitter Crawlr URL: ==================
 * 
 * http://localhost:8080/socialMediaMetaCrawlr-0.0.1-SNAPSHOT/search?source=twitter&keywords=Airport&bbox=-140,16,-100,56&minTime=2008-12-01&maxTime=2014-01-13
 * 
 * SOS URL: ========
 * 
 * Register Sensor: ========
 * 
 * http://localhost:8080/socialMediaMetaCrawlr-0.0.1-SNAPSHOT/registerSensor?procedure=flickr&offering=flickr&observedProperty=photos
 * 
 * http://localhost:8080/socialMediaMetaCrawlr-0.0.1-SNAPSHOT/registerSensor?procedure=twitter&offering=twitter&observedProperty=tweeds
 * 
 * Query: ========
 * 
 * http://ows.dev.52north.org:8080/52n-wfs-webapp/sos/kvp?service=WFS&version=2.0.0&request=GetFeature&namespaces=xmlns(om%2Chttp%3A%2F%2Fwww.opengis.net%2Fom%2F2.0)&typenames=om%3AObservation&filter=%3CFilter%20xmlns%3D%22http%3A%2F%2Fwww.opengis.net%2Ffes%2F2.0%22%20xmlns%3Agml%3D%22http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2%22%20xmlns%3Axsi%3D%22http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema-instance%22%20xsi%3AschemaLocation%3D%22http%3A%2F%2Fwww.opengis.net%2Ffes%2F2.0%20http%3A%2F%2Fschemas.opengis.net%2Ffilter%2F2.0.0%2Ffilter.xsd%20http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2%20http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2.1%2Fbase%2Fgml.xsd%22%3E%3CDuring%3E%3CValueReference%3Eom%3AphenomenonTime%3C%2FValueReference%3E%3Cgml%3ATimePeriod%20gml%3Aid%3D%22tp_1%22%3E%3Cgml%3AbeginPosition%3E2008-12-01T14%3A00%3A00.000%2B01%3A00%3C%2Fgml%3AbeginPosition%3E%3Cgml%3AendPosition%3E2013-12-30T14%3A05%3A00.000%2B01%3A00%3C%2Fgml%3AendPosition%3E%3C%2Fgml%3ATimePeriod%3E%3C%2FDuring%3E%3C%2FFilter%3E
 * 
 * @author <a href="mailto:broering@52north.org>Arne Broering</a>
 */
@Path("/")
public class PhotoCrawlrResource implements Constants {

    private static final Logger log = LoggerFactory.getLogger(TwitterCrawlrImpl.class);

    public PhotoCrawlrResource() {
    }

    @GET
    @Path("/registerSensor")
    @Produces(MediaType.TEXT_HTML)
    public Response doRegisterSensor(@QueryParam(PROCEDURE) String procedureID,
            @QueryParam(OFFERING) String offeringID, @QueryParam(OBSERVED_PROPERTY) String observedProperty)
            throws IOException {
        new SosFlickrUploadrImpl().registerProcedure(procedureID, offeringID, observedProperty);

        String output = "Procedure registered.";
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_HTML)
    public Response doSearch(@QueryParam(KEYWORDS) String keywords, @QueryParam(BBOX) BoundingBoxParam bbox,
            @QueryParam(MIN_TIME) TimeParam minTime, @QueryParam(MAX_TIME) TimeParam maxTime,
            @QueryParam(SOURCE) String source) throws IOException, SAXException, FlickrException,
            ParserConfigurationException, ExceptionReport, OXFException {
        if (isNullOrEmpty(keywords)) {
            throw new MissingParameterException(KEYWORDS);
        }

        if (bbox == null) {
            throw new MissingParameterException(BBOX);
        }
        double minLongitude = bbox.getMinLongitude();
        double minLatitude = bbox.getMinLatitude();
        double maxLongitude = bbox.getMaxLongitude();
        double maxLatitude = bbox.getMaxLatitude();

        if (minTime == null) {
            throw new MissingParameterException(MIN_TIME);
        }
        Date minTakenDate = minTime.getTime().getCalendar().getTime();

        if (maxTime == null) {
            throw new MissingParameterException(MAX_TIME);
        }
        Date maxTakenDate = maxTime.getTime().getCalendar().getTime();

        List<Entry> photoList = null;

//        if (isNotNullOrEmpty(source)) {
//           crawlr = CrawlrFactory.getCrawlr(source);
//            sosUploadr = SosUploadrFactory.getSosUploadrImpl(source);
//            if (TWITTER.equals(source)) {
//                
//                try {
//                    photoList =
//                            new TwitterCrawlrImpl().crawlForEntities(minLongitude, minLatitude, maxLongitude,
//                                    maxLatitude, minTakenDate, maxTakenDate, keywords.split(","));
//                    new SosFlickrUploadrImpl().uploadEntries(photoList);
//                } catch (TwitterException e) {
//                    log.error(e.getMessage(), e);
//                }
//            } else if (FLICKR.equals(source)) {
//                photoList =
//                        new FlickrCrawlrImpl().crawlForEntities(minLongitude, minLatitude, maxLongitude, maxLatitude,
//                                minTakenDate, maxTakenDate, keywords.split(","));
//               
//            } else {
//                throw new InvalidParameterException(String.format("The requested source '%s' is not supported!", source));
//            }
//        } else {
//            throw new MissingParameterException(source);
//        }
        ICrawlr crawlr = null;
        SosUploadr sosUploadr = null;
        List<String> keywordList = Arrays.asList(keywords.split(","));
        String output = "";
        if (isNotNullOrEmpty(source)) {
            crawlr = CrawlrFactory.getCrawlr(source);
            sosUploadr = SosUploadrFactory.getSosUploadrImpl(source);
            try {
                List<Entry> entryList = crawlr.crawlForEntities(minLongitude, minLatitude, maxLongitude,
                        maxLatitude, minTakenDate, maxTakenDate, keywordList);
                sosUploadr.uploadEntries(entryList, keywordList);
                output = String.format("<b>Downloaded the following metadata from %s and uploaded them to the SOS:</b> <br>", source);
                for (Entry entries : entryList) {
                    output += entries + "<br>";
                }
               
            } catch (Exception e) {
                log.error(String.format("Error while crawling data from %s", source), e);
            }
        } else {
            throw new MissingParameterException(source);
        }
        return Response.status(200).entity(output).build();
       
    }
    
    private boolean isNotNullOrEmpty(String string) {
        return string != null && !string.isEmpty();
    }
    
    private boolean isNullOrEmpty(String string) {
        return !isNotNullOrEmpty(string);
    }

}
