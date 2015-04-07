package org.n52.crawlr.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.n52.crawlr.entities.Entry;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aetrion.flickr.tags.Tag;

public abstract class SosUploadr {

    private static final Logger log = LoggerFactory.getLogger(SosUploadr.class);

    // protected static final String SOS_URL =
    // "http://ows.dev.52north.org:8080/52n-wfs-webapp/sos/soap";
    
    public static final String SOS_URL_KEY = "sos.url";
    
    protected static final String DEFAULT_SOS_URL = "http://localhost:8080/52n-sos-webapp/sos";

    protected static final String SOS_VERSION = "2.0.0";

    protected static final String INSERT_OBS_TEMPLATE = "InsertObservationTemplate.xml";

    protected static final String INSERT_SENSOR_TEMPLATE = "InsertSensorTemplate.xml";

    protected static final String PL_OFFERING_ID = "@OFFERING_ID@";

    protected static final String PL_OBSERVATION_GML_ID = "@OBSERVATION_GML_ID@";

    protected static final String PL_PHENOMENON_TIME = "@PHENOMENON_TIME@";

    protected static final String PL_RESULT_TIME = "@RESULT_TIME@";

    protected static final String PL_PROCEDURE_ID = "@PROCEDURE_ID@";

    protected static final String PL_OBSERVED_PROPERTY = "@OBSERVED_PROPERTY@";

    protected static final String PL_FOI_ID = "@FOI_ID@";

    protected static final String PL_FOI_NAME = "@FOI_NAME@";

    protected static final String PL_OBSERVATION_DESCRIPTION = "@OBSERVATION_DESCRIPTION@";

    protected static final String PL_FOI_POINT_ID = "@FOI_POINT_ID@";

    protected static final String PL_FOI_LONGITUDE = "@FOI_LONGITUDE@";

    protected static final String PL_FOI_LATITUDE = "@FOI_LATITUDE@";

    protected static final String PL_RESULT = "@RESULT@";

    protected static final String PROPERTY_HUMAN_VISUAL_PERCEPTION =
            "http://www.opengis.net/def/property/humanVisualPerception";
    
    protected static final String OFFERING_PREFIX = "http://www.opengis.net/def/offering/";

    private Set<String >insertedProcedures = new HashSet<String>();

    /**
     * Calls InsertSensor to register procedureID.
     * 
     * @throws IOException
     */
    public void registerProcedure(String procedureID, String offeringID, String observedProperty) throws IOException {
        StringBuilder insertSensorTemplate = getInsertSensorTemplate();

        replace(insertSensorTemplate, PL_PROCEDURE_ID, procedureID);
        replace(insertSensorTemplate, PL_OFFERING_ID, offeringID);
        replace(insertSensorTemplate, PL_OBSERVED_PROPERTY, observedProperty);

        // now send to SOS:
        log.info("Inserting procedure: " + procedureID + " - with offering: " + offeringID);
        String response = IOHelper.readText(sendPostMessage(DEFAULT_SOS_URL, insertSensorTemplate.toString()));
        log.info(response);
    }

    public abstract void uploadEntries(List<Entry> entryList, List<String> keywordList) throws ExceptionReport, OXFException, IOException;
    
    protected void uploadEntry(Entry entry, StringBuilder insertObservationTemplate, String id, String procedurePrefix) throws IOException {
        
        String username = entry.getUserName().trim();
        String result = entry.getValue().trim();
        Date resultTime = entry.getDatePosted();
        Date samplingTime = entry.getDateTaken();
        Double longitude = entry.getLongitude();
        Double latitude = entry.getLatitude();
        String foiName = entry.getTitle().trim();
        String observationDescription = entry.getDescription().trim();
        if  (entry.isSetTags()) {
            observationDescription += "," + concat(entry.getTags());
        }
        
        String procedureID = procedurePrefix + username;
        String offeringID  = OFFERING_PREFIX + username;
        String observedProperty = PROPERTY_HUMAN_VISUAL_PERCEPTION;
        
        checkIfSensorIsInserted(procedureID, offeringID, observedProperty);
        
        replace(insertObservationTemplate, PL_OBSERVATION_GML_ID, id + "-observation");
        replace(insertObservationTemplate, PL_OBSERVATION_DESCRIPTION, observationDescription);
        replace(insertObservationTemplate, PL_OFFERING_ID, offeringID);
        replace(insertObservationTemplate, PL_PROCEDURE_ID, procedureID);
        replace(insertObservationTemplate, PL_OBSERVED_PROPERTY, observedProperty);
        replace(insertObservationTemplate, PL_OBSERVATION_GML_ID, entry.getID());
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        String resultTimeAsISO = df.format(resultTime);
        replace(insertObservationTemplate, PL_RESULT_TIME, resultTimeAsISO);
        
        String samplingTimeAsISO = df.format(samplingTime);
        replace(insertObservationTemplate, PL_PHENOMENON_TIME, samplingTimeAsISO);
        
        replace(insertObservationTemplate, PL_FOI_ID, id + "-feature");
        replace(insertObservationTemplate, PL_FOI_LATITUDE, latitude.toString());
        replace(insertObservationTemplate, PL_FOI_LONGITUDE, longitude.toString());
        replace(insertObservationTemplate, PL_FOI_NAME, foiName);
        replace(insertObservationTemplate, PL_FOI_POINT_ID, "foiPointID");
        
        replace(insertObservationTemplate, PL_RESULT, result);
        
        
        // now send to SOS:
        log.info("sending InsertObservation to SOS: " + insertObservationTemplate.toString());
        
        String response = IOHelper.readText(sendPostMessage(DEFAULT_SOS_URL, insertObservationTemplate.toString()));
        
        log.info(response);
        
    }

    private void checkIfSensorIsInserted(String procedureID, String offeringID, String observedProperty) throws IOException {
        if (!insertedProcedures.contains(procedureID)) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("service", "SOS"));
            params.add(new BasicNameValuePair("version", SOS_VERSION));
            params.add(new BasicNameValuePair("request", "DescribeSensor"));
            params.add(new BasicNameValuePair("procedure", procedureID));
            params.add(new BasicNameValuePair("procedureDescriptionFormat", "http://www.opengis.net/sensorML/1.0.1"));
            
            String response = IOHelper.readText(sendGetMessage(DEFAULT_SOS_URL, URLEncodedUtils.format(params, "utf-8")));
            if (response != null) {
                if (response.contains("ExceptionReport")) {
                    registerProcedure(procedureID, offeringID, observedProperty);
                    insertedProcedures.add(procedureID);
                } else if (response.contains("DescribeSensorResponse")) {
                    insertedProcedures.add(procedureID);
                }
            }
        }
    }

    /**
     * Helper method.
     */
    protected String concat(Collection<String> collection) {
        String result = "";

        if (collection != null) {
            Object[] array = new Object[0];
            array = collection.toArray(array);
            if (array.length >= 1) {
                if (array[0] instanceof Tag) {
                    Tag tag = (Tag) array[0];
                    result += tag.getValue();
                } else {
                    result += array[0].toString();
                }
            }
            for (int i = 1; i < array.length; i++) {
                if (array[0] instanceof Tag) {
                    Tag tag = (Tag) array[i];
                    result += ", " + tag.getValue();
                } else {
                    result += ", " + array[i].toString();
                }
            }
        }
        return result;
    }

    /**
     * Helper method.
     */
    protected StringBuilder replace(StringBuilder builder, String replaceWhat, String replaceWith) {
        int indexOfTarget = -1;
        while ((indexOfTarget = builder.indexOf(replaceWhat)) > 0) {
            builder.replace(indexOfTarget, indexOfTarget + replaceWhat.length(), replaceWith);
        }
        return builder;
    }

    /**
     * Helper method.
     */
    protected InputStream sendPostMessage(String serviceURL, String request) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(serviceURL + "/soap");

        post.setEntity(new StringEntity(request, ContentType.create("application/soap+xml", "UTF-8")));

        HttpResponse response = httpClient.execute(post);

        return response.getEntity().getContent();
    }
    
    /**
     * Helper method.
     */
    protected InputStream sendGetMessage(String serviceURL, String request) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(serviceURL+ "/kvp?" + request);
        
        HttpResponse response = httpClient.execute(get);

        return response.getEntity().getContent();
    }

    protected StringBuilder getInsertObservationTemplate() throws IOException {
        return getInsertionTemplate(INSERT_OBS_TEMPLATE);
    }

    protected StringBuilder getInsertSensorTemplate() throws IOException {
        return getInsertionTemplate(INSERT_SENSOR_TEMPLATE);
    }

    protected StringBuilder getInsertionTemplate(String template) throws IOException {
        return new StringBuilder(IOHelper.readText(SosUploadr.class.getResourceAsStream(template)));
    }
    
    protected String getSosUrl() throws IOException {
        return PropertiesLoader.getInstance().getProperties().getProperty(SOS_URL_KEY, DEFAULT_SOS_URL);
    }

}