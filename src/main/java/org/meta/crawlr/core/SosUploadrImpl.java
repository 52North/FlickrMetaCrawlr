package org.meta.crawlr.core;

import java.util.List;

import org.meta.crawlr.entities.FlickrPhoto;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.sos.adapter.wrapper.SOSWrapper;
import org.n52.oxf.sos.adapter.wrapper.SosWrapperFactory;
import org.n52.oxf.sos.request.InsertObservationParameters;

public class SosUploadrImpl {

	private static final String SOS_URL = "http://my-SOS"; 
	private static final String SOS_VERSION = "2.0.0";
	
	public void uploadPhotos (List<FlickrPhoto> photoList) throws ExceptionReport, OXFException {
		
//		InsertObservationParameters i = null;
//		org.n52.oxf.sos.request.v200.InsertObservationParameters i2;
//		org.n52.oxf.sos.adapter.wrapper.builder.InsertObservationParameters i3;
//		
//		i2 = new org.n52.oxf.sos.request.v200.InsertObservationParameters(observationParameters, offerings);
//		
//		SOSWrapper sosWrapper = SosWrapperFactory.newInstance(SOS_URL, SOS_VERSION);
//		sosWrapper.doInsertObservation(i);
//		
//		for (FlickrPhoto photo : photoList) {
//			
//			photo.getPhotoID();			// --> id
//			photo.getPhotoURL(); 		// --> result
//			photo.getPhotoDatePosted();	// --> resultTime
//			photo.getPhotoDateTaken();	// --> samplingTime
//			photo.getPhotoLatitude();	// --> foi.latitude
//			photo.getPhotoLongitude();	// --> foi.longitude
//			photo.getUserId();			// --> procedure
//
//			photo.getPhotoTitle();		// --> ???
//			photo.getPhotoTags();		// --> ???
//			
//										// --> observedProperty
//				
//		}
	}
	
}
