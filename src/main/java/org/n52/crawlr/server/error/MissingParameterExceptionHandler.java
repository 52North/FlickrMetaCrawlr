package org.n52.crawlr.server.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MissingParameterExceptionHandler implements
		ExceptionMapper<MissingParameterException> {

	@Override
	public Response toResponse(MissingParameterException ex) {
		String msg = ex.getMessage();
		StringBuilder response = new StringBuilder("<response>");
		response.append("<status>failed<status>");
		response.append("<message>" + msg + "<message>");
		response.append("<response>");

		return Response.serverError().entity(response.toString()).build();
	}
}
