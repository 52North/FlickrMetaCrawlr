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

import java.util.ServiceLoader;

import org.meta.crawlr.core.PhotoCrawlrImpl;
import org.meta.crawlr.guice.JerseyModule;
import org.meta.crawlr.guice.JerseyResourceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class ServiceLoaderConfigurationModule extends AbstractModule {
    
	private static final Logger log = LoggerFactory.getLogger(ServiceLoaderConfigurationModule.class);
	
	@Override
    protected void configure() {
		
//		install(new JerseyModule());
//		install(new JerseyResourceModule());
		
//        for (Module m : ServiceLoader.load(Module.class)) {
//        	log.info("Installing module: " + m);
//        	System.out.println("Installing module: " + m);
//            install(m);
//        }
    }
}
