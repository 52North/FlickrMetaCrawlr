package org.meta.crawlr.server;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereFramework;

@Path("/stream")
@Produces("application/json")
public class StreamingPhotoCrawlr {
	
    @Suspend
    @GET
    public String suspend() {
    	return "";
    }
    
    @Broadcast(writeEntity = false)
    @POST
    public Response broadcast(String message) {
    	return Response.status(200).entity("message length: " + message.length()).build();
    }
}
