package com.upcacm.zwedit;

import java.util.Properties;

import javax.websocket.server.ServerContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer; 

import com.upcacm.zwedit.event.EventServlet;
import com.upcacm.zwedit.controller.ZweditController;

import com.upcacm.zwedit.util.RedisPool;

public class ZweditServer {
    
    private static Logger logger = LoggerFactory.getLogger(ZweditServer.class);

    public static void main(String[] args) throws Exception {
        
        Properties config = new Properties();
        
        config.load(ZweditServer.class.getClassLoader().getResourceAsStream("zwedit.properties")); 
        
        int backendPort = 7301;
        
        int maxIdle = 300000;
        
        try {
            backendPort = Integer.parseInt(config.getProperty("jetty_server_port", "7301"));
            maxIdle = Integer.parseInt(config.getProperty("jetty_max_idle", "300000"));
            RedisPool.init(config);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    
        Server server = new Server();
        
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(backendPort);
        connector.setIdleTimeout(maxIdle);        
        
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
