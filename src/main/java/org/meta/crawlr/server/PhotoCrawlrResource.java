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

import com.google.inject.Inject;

@Path("/")
public class PhotoCrawlrResource  {
    public static final String SENSOR = "sensor";
    
    @Inject
    public PhotoCrawlrResource() {
    }

    @GET
    @Produces({ MediaTypes.XML_RDF})
    public String doStuff() {
        return "hello";
    }

    @PathParam(SENSOR)
    public String sensor() {
        return "sensors";
    }
}
