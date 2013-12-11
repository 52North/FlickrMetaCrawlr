package org.n52.flickr.crawlr.server.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<ApplicationException> {

	@Override
	public Response toResponse(ApplicationException ex) {

		String msg = ex.getMessage();
		String internalError = ex.getInternalErrorMessage();
		StringBuilder response = new StringBuilder("<response>");
		response.append("<status>failed<status>");
		response.append("<message>" + msg + "<message>");
		response.append("<internalError>" + internalError + "<internalError>");
		response.append("<response>");
		return Response.serverError().entity(response.toString()).build();
	}
}
