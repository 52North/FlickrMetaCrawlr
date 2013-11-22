package org.meta.crawlr.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.google.inject.Singleton;

@Singleton
public class PhotoCrawlrServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
	
       resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
       OutputStream out = resp.getOutputStream();
       
       String hello = "halllllli halllllo";
       out.write(hello.getBytes());
	}
}
