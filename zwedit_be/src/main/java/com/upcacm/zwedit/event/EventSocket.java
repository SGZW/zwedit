package com.upcacm.zwedit.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket
public class EventSocket {
    
    private final Logger logger = LoggerFactory.getLogger(EventSocket.class);
    
    @OnWebSocketConnect
    public void onConnect(Session session) {
    	System.out.println(session);
    }
    
    @OnWebSocketMessage
    public void onText(Session session, String message) {
        if (session.isOpen()) {
            System.out.println(message);
            session.getRemote().sendString(message, null);
        }
    }
    
    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
	logger.error("websocket error: ", cause);
    }
    
    @OnWebSocketClose
    public void onClose(Session session, int closeCode, String closeReason) {}                       
}
