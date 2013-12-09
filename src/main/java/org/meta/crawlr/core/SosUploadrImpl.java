package org.meta.crawlr.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.meta.crawlr.entities.FlickrPhoto;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.adapter.SOSRequestBuilderFactory;
import org.n52.oxf.sos.adapter.TestSOSAdapter;
import org.n52.oxf.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosUploadrImpl {

	private static final Logger log = LoggerFactory.getLogger(SosUploadrImpl.class);
	
	private static final String SOS_URL = "http://ows.dev.52north.org:8080/52n-wfs-webapp/sos/soap"; 
	private static final String SOS_VERSION = "2.0.0";
	private static final String INSERT_OBS_TEMPLATE = "InsertObservationTemplate.xml";
	
	private static final String OFFERING_ID = "@OFFERING-ID@";
	private static final String OBSERVATION_GML_ID = "@OBSERVATION_GML_ID@";
	private static final String PHENOMENON_TIME = "@PHENOMENON_TIME@";
	private static final String RESULT_TIME = "@RESULT_TIME@";
	private static final String PROCEDURE_ID = "@PROCEDURE_ID@";
	private static final String OBSERVED_PROPERTY = "@OBSERVED_PROPERTY@";
	private static final String FOI_ID = "@FOI_ID@";
	private static final String FOI_NAME = "@FOI_NAME@";
	private static final String FOI_DESCRIPTION = "@FOI_DESCRIPTION@";
	private static final String FOI_POINT_ID = "@FOI_POINT_ID@";
	private static final String FOI_LONGITUDE = "@FOI_LONGITUDE@";
	private static final String FOI_LATITUDE = "@FOI_LATITUDE@";
	private static final String RESULT = "@RESULT@";
	
	
	public static void main(String[] args) throws Exception {
		String request = IOHelper.readText(SosUploadrImpl.class.getResourceAsStream("InsertSensorTemplate2.xml"));
		
		String response = IOHelper.readText(sendPostMessage(SOS_URL, request));
		
		System.out.println(response);
	}
	
	public void uploadPhotos (List<FlickrPhoto> photoList) throws ExceptionReport, OXFException, IOException {
				
		// one static procedure / offering
		final String offeringID  	  = "http://www.52north.org/sos/offering/flickr";
		final String procedureID 	  = "http://www.52north.org/sos/procedure/flickr";
		final String observedProperty = "http://www.52north.org/sos/observableProperty/photo";
		
		StringBuilder insertObservationTemplate = new StringBuilder(IOHelper.readText(SosUploadrImpl.class.getResourceAsStream(INSERT_OBS_TEMPLATE)));

		for (FlickrPhoto photo : photoList) {
			
			String observationGmlId = photo.getPhotoID();
			String result = photo.getPhotoURL();
			Date resultTime = photo.getPhotoDatePosted();
			Date samplingTime = photo.getPhotoDateTaken();
			Float latitude = photo.getPhotoLatitude();
			Float longitude = photo.getPhotoLongitude();
			String foiName = photo.getPhotoTitle();
			String foiDescription = concat(photo.getPhotoTags());
			String userID = photo.getUserId();
			
			replace(insertObservationTemplate, OBSERVATION_GML_ID, observationGmlId);
			replace(insertObservationTemplate, OFFERING_ID, offeringID);
			replace(insertObservationTemplate, PROCEDURE_ID, procedureID);
			replace(insertObservationTemplate, OBSERVED_PROPERTY, observedProperty);
			replace(insertObservationTemplate, OBSERVATION_GML_ID, photo.getPhotoID());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
			String resultTimeAsISO = df.format(resultTime);
			replace(insertObservationTemplate, RESULT_TIME, resultTimeAsISO);
			
			String samplingTimeAsISO = df.format(samplingTime);
			replace(insertObservationTemplate, PHENOMENON_TIME, samplingTimeAsISO);
			
			replace(insertObservationTemplate, FOI_ID, "foiID");
			replace(insertObservationTemplate, FOI_DESCRIPTION, foiDescription);
			replace(insertObservationTemplate, FOI_LATITUDE, latitude.toString());
			replace(insertObservationTemplate, FOI_LONGITUDE, longitude.toString());
			replace(insertObservationTemplate, FOI_NAME, foiName);
			replace(insertObservationTemplate, FOI_POINT_ID, "foiPointID");
			
			replace(insertObservationTemplate, RESULT, result);
			
			
			// now send to SOS:
			log.info("sending: " + insertObservationTemplate.toString());
			
			String response = IOHelper.readText(sendPostMessage(SOS_URL, insertObservationTemplate.toString()));
			
			log.info(response);
		}
	}
	
	/**
	 * Helper method.
	 */
	private static String concat(Collection<String> collection) {
		String result = "";
		
		if (collection != null) {
			String[] array = new String[collection.size()];
			array = collection.toArray(array);
			
			if (array.length >= 1) {
				result += array[0];
			}
			for (int i=1; i<array.length; i++) {
				result += array[i];
			}
		}
		return result;
	}
	
	/**
     * Helper method.
     */
	protected static StringBuilder replace(StringBuilder builder,
            String replaceWhat,
            String replaceWith)
    {
        int indexOfTarget = -1;
        while ((indexOfTarget = builder.indexOf(replaceWhat)) > 0) {
            builder.replace(indexOfTarget, indexOfTarget + replaceWhat.length(), replaceWith);
        }
        return builder;
    }
	
	/**
	 * Helper method.
	 */
	private static InputStream sendPostMessage(String serviceURL, String request) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost  post = new HttpPost (serviceURL);

        post.setEntity(new StringEntity(request, "text/xml", "UTF-8"));

        HttpResponse response = httpClient.execute(post);

        return response.getEntity().getContent();
    }
}