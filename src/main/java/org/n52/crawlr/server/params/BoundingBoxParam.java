package org.n52.crawlr.server.params;

/**
 * Class represents a bounding box parameter used in the search query.
 * 
 * @author <a href="mailto:broering@52north.org>Arne Broering</a>
 */
public class BoundingBoxParam {

	double minLongitude;
	double minLatitude;
	double maxLongitude;
	double maxLatitude;
	
	public BoundingBoxParam(String param) {
		initAttributes(param);
	}

	private void initAttributes(String param) {
		String[] bboxArray = param.split(",");
		
		if (bboxArray.length != 4) {
			throw new IllegalArgumentException("Bounding box parameter must have 4 values separated by a ',':" +
					" <minLongitude,minLatitude,maxLongitude,maxLatitude>");
		}
		
		this.minLongitude = Double.parseDouble(bboxArray[0]);
		this.minLatitude = Double.parseDouble(bboxArray[1]);
		this.maxLongitude = Double.parseDouble(bboxArray[2]);
		this.maxLatitude = Double.parseDouble(bboxArray[3]);
	}

	
	
	//
	// GETTERS:
	//
	
	public double getMinLongitude() {
		return minLongitude;
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}
	
	
}
