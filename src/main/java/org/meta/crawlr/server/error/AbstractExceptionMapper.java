package org.meta.crawlr.server.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

	@Override
	public Response toResponse(T ex) {
		return Response.status(500).entity(ex.getMessage()).type("text/plain").build();
	}
}
