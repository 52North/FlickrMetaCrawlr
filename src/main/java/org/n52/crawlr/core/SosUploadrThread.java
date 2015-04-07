package org.n52.crawlr.core;

import java.io.IOException;
import java.util.List;

import org.n52.crawlr.entities.Entry;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosUploadrThread implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SosUploadrThread.class);
	
	private List<Entry> entryList;
	private List<String> keywords;
	private SosUploadr sosUploadr;
	
	public SosUploadrThread (List<Entry> entryList, List<String> keywords, SosUploadr sosUploadr) {
		this.entryList = entryList;
		this.keywords = keywords;
		this.sosUploadr = sosUploadr;
	}
	
	@Override
	public void run() {
	    if (sosUploadr != null) {
		try {
			sosUploadr.uploadEntries(entryList, keywords);
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

}
