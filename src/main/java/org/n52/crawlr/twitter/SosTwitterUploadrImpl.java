package org.n52.crawlr.twitter;

import java.io.IOException;
import java.util.List;

import org.n52.crawlr.core.Constants;
import org.n52.crawlr.core.SosUploadr;
import org.n52.crawlr.entities.Entry;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosTwitterUploadrImpl extends SosUploadr {

    private static final Logger log = LoggerFactory.getLogger(SosTwitterUploadrImpl.class);

    @Override
    public void uploadEntries(List<Entry> entryList, List<String> keywordList) throws ExceptionReport, OXFException,
            IOException {
        for (Entry entry : entryList) {
            entry.addToDescription(keywordList);
            StringBuilder insertObservationTemplate = getInsertObservationTemplate();
            String tweedId = "tweed-" + entry.getID();
            uploadEntry(entry, insertObservationTemplate, tweedId, "http://www.twitter.com/tweeds/");
        }
    }

    @Override
    protected StringBuilder getInsertObservationTemplate() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("/").append(Constants.TWITTER).append("_").append(INSERT_OBS_TEMPLATE);
        return getInsertionTemplate(builder.toString());
    }

    @Override
    protected StringBuilder getInsertSensorTemplate() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("/").append(Constants.TWITTER).append("_").append(INSERT_SENSOR_TEMPLATE);
        return getInsertionTemplate(builder.toString());
    }

}
