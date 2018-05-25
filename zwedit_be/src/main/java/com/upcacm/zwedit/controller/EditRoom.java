package com.upcacm.zwedit.controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

public class EditRoom {

    private volatile int sessionId;

    private volatile String text;
    
    private ConcurrentHashMap<String, Session> store;
    
    public EditRoom() {
        sessionId = 0;
	store = new ConcurrentHashMap<String, Session>();
    }
    
    public synchronized String getNewSessionId() {
       sessionId += 1;
       return String.valueOf(sessionId);
    }
         
    public synchronized String getText() {
 	return this.text;
    }
    
    public synchronized void addSession(String id, Session session) {
	store.put(id, session);
    }
    
    public synchronized void process(String newText) {
        this.text = newText;
        HashSet<String> del = new HashSet<String>();
        for(Map.Entry<String, Session> entry: store.entrySet()) {
            String id = entry.getKey();
            Session session = entry.getValue();
            if(!session.isOpen()) {
            	del.add(id);	
            } else {
               session.getRemote().sendString(newText, null);
            }
        }
        for(String d: del) {
	    if(store.containsKey(d)) {
            	store.remove(d);
            }
        }
    }
}
