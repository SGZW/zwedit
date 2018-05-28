package com.upcacm.zwedit.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.websocket.api.Session;

import com.upcacm.zwedit.ot.TextOperation;
import com.upcacm.zwedit.util.Pair;
import com.upcacm.zwedit.util.JsonUtil;

public class EditRoom {

    private final Logger logger = LoggerFactory.getLogger(EditRoom.class);
    
    private volatile int sessionId = 0;

    private volatile String text = "";
    
    private HashMap<String, Session> store = null;

    public String roomUrl = "";

    private ArrayList<TextOperation> ops = null;

    private HashMap<String, Integer> lastOp = null;
    
    public EditRoom(String roomUrl) {
        this.roomUrl = roomUrl;
        sessionId = 0;
        text = "";
	    store = new HashMap<String, Session>();
        ops = new ArrayList<TextOperation>();
        lastOp = new HashMap<String, Integer>();
    }

    public Integer getLastRevision(String sid) {
        return this.lastOp.get(sid);
    }
    
    public void saveOp(String sid, TextOperation op) {
        this.lastOp.put(sid, new Integer(ops.size()));
        this.ops.add(op);
    }
    
    public synchronized String getNewSessionId() {
        sessionId += 1;
        return String.valueOf(sessionId);
    }
         
    public synchronized Pair<Integer, String> getNewResponse() {
        return new Pair(new Integer(ops.size()), text);
    }
    
    public synchronized void addSession(String sid, Session session) {
	    store.put(sid, session);
    }
        
    public String getResponse(String sid, String op) {
        return "{ \"type\": \"merge\",\"sid\": \"" + sid + "\", \"op\" :" + op + "}";   
    }
    
    public synchronized void process(String sid, int revision, TextOperation nop) {
        
        Integer last = getLastRevision(sid);
        if (last != null && last.intValue() >= revision) return;
        try {
            for (int i = revision; i < ops.size(); ++i) {
                Pair<TextOperation, TextOperation> p = TextOperation.transform(nop, ops.get(i));
                nop = p.getA();
            }
            this.text = nop.apply(this.text);
        
            HashSet<String> del = new HashSet<String>();
            String jsonOp = "";
            for (Map.Entry<String, Session> entry: store.entrySet()) {
                String id = entry.getKey();
                Session session = entry.getValue();
                if (!session.isOpen()) {
            	    del.add(id);
                    continue;	
                }
                if (!id.equals(sid)) {
                    if (jsonOp.equals("")) jsonOp = JsonUtil.dump(nop);
                    session.getRemote().sendString(getResponse(sid, jsonOp), null);
                } else session.getRemote().sendString(getResponse(sid, "[]"), null);
            }
            for (String d: del) {
	            if(store.containsKey(d)) store.remove(d);
                if(lastOp.containsKey(d)) store.remove(d);
            }
        } catch (Exception e) {
            logger.error("process error: " + e.getMessage());       
        }
    }
}
