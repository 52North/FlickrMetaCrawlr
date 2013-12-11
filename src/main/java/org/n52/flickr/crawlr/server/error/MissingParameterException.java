package org.n52.flickr.crawlr.server.error;

public class MissingParameterException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public MissingParameterException(String parameterName) {
		super("Parameter '" + parameterName + "' has not been specified.");
	}
}