package org.meta.crawlr.core;

import java.io.IOException;
import java.util.List;

import org.meta.crawlr.entities.FlickrPhoto;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosUploadrThread implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SosUploadrThread.class);
	
	private List<FlickrPhoto> photoList;
	
	public SosUploadrThread (List<FlickrPhoto> photoList) {
		this.photoList = photoList;
	}
	
	@Override
	public void run() {
		SosUploadrImpl sosUploadr = new SosUploadrImpl();
		try {
			
			sosUploadr.uploadPhotos(photoList);
			
		} catch (ExceptionReport e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (OXFException e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
