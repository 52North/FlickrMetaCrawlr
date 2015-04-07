package org.n52.crawlr.server.params;

import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.oxf.valueDomains.time.TimePosition;

/**
 * Class represents a time parameter used in the search query.
 * 
 * @author <a href="mailto:broering@52north.org>Arne Broering</a>
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
