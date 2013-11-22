package org.meta.crawlr.server.params;

import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.oxf.valueDomains.time.TimePosition;

/**
 * Class represents a time parameter used in the search query.
 * 
 * @author Arne
 */
public class TimeParam {

	private ITimePosition time;
	
	/**
	 * @param param ISO 8601 compliant String.   
	 */
	public TimeParam(String param) {
		time = (TimePosition) TimeFactory.createTime(param);
	}
	
	public ITimePosition getTime() {
		return time;
	}
	
}
