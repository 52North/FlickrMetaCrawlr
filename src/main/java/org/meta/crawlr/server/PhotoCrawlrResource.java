/*
 * Copyright (C) 2013 The enviroCar project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meta.crawlr.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class PhotoCrawlrResource  {
    
    public PhotoCrawlrResource() {
    }

    @GET
	@Path("/search")
    @Produces(MediaType.TEXT_PLAIN)
	public Response doSearch(
			@QueryParam("keywords") String keywords,
			@QueryParam("bbox") BoundingBoxParam bbox,
			@QueryParam("minTime") TimeParam minTime,
			@QueryParam("maxTime") TimeParam maxTime)
    {
    	
    	
    	
    	
    	
		String output = "";
		
		return Response.status(200).entity(output).build();
	}

}
