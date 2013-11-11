package org.meta.crawlr.core;

import java.util.List;

import org.meta.crawlr.entities.*;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.sos.adapter.wrapper.SOSWrapper;

public class SosUploadrImpl {

	private static final String SOS_URL = "http://my-SOS"; 
	private static final String SOS_VERSION = "2.0.0";
	
	public void uploadPhotos (List<FlickrPhoto> photoList) throws ExceptionReport, OXFException {
		
		for (FlickrPhoto photo : photoList) {
			
			photo.getPhotoID();			// --> id
			photo.getPhotoURL(); 		// --> result
			photo.getPhotoDatePosted();	// --> resultTime
			photo.getPhotoDateTaken();	// --> samplingTime
			photo.getPhotoLatitude();	// --> foi.latitude
			photo.getPhotoLongitude();	// --> foi.longitude
			photo.getUserId();			// --> procedure

			photo.getPhotoTitle();		// --> 
			photo.getPhotoTags();		// --> 
			
										// --> observedProperty
						
			SOSWrapper sos = SOSWrapper.createFromCapabilities(SOS_URL, SOS_VERSION);
			
//			ObservationParameters obsParams = new MeasurementObservationParameters();
//			obsParams.addFoiDescription(foiDescription);
//			
//			InsertObservationParameterBuilder_v100 builder = new InsertObservationParameterBuilder_v100(assignedSensorId, obsParameter);
//			sos.doInsertObservation(builder);
		}
	}
	
}
