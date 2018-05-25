package com.upcacm.zwedit;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer; 

import com.upcacm.zwedit.event.EventServlet;
import com.upcacm.zwedit.controller.ZweditController;

public class ZweditServer {

    public static void main(String[] args) throws Exception {
        
        //controller init
        ZweditController.getZweditController();
       
        Server server = new Server();
        
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(8080);
        connector.setIdleTimeout(300000);        
        
        server.addConnector(connector);
     
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
	
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");
         
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");  
        sh.setInitParameter("com.sun.jersey.config.property.packages", "com.upcacm.zwedit.rest"); 
        context.addServlet(sh, "/*");
       
        server.setHandler(context);
         
        server.start();
        server.join();
    }
}
