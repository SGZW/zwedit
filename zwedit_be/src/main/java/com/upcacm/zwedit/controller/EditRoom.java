package com.upcacm.zwedit.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.eclipse.jetty.websocket.api.Session;

import com.upcacm.zwedit.ot.TextOperation;
import com.upcacm.zwedit.util.Pair;
import com.upcacm.zwedit.util.JsonUtil;

public class EditRoom {
 
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
         
    public synchronized void addSessionAndSendInitResponse(String sid, Session session) throws Exception {
        store.put(sid, session);
        LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>();
        ret.put("type", "new");
        ret.put("revision", new Integer(this.ops.size()));
        ret.put("text", this.text);
        String res = JsonUtil.dumpMap(ret);
        session.getRemote().sendString(res, null);
    }
    
    public String getResponse(String sid, LinkedList actions) throws Exception {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>();
        ret.put("type", "merge");
        ret.put("sid", sid);
        ret.put("actions", actions);
        return JsonUtil.dumpMap(ret);
    }
    
    public synchronized void process(String sid, int revision, TextOperation nop) throws Exception {
        
        Integer last = getLastRevision(sid);
        if (last != null && last.intValue() >= revision) return;
        for (int i = revision; i < ops.size(); ++i) {
            Pair<TextOperation, TextOperation> p = TextOperation.transform(nop, ops.get(i));
            nop = p.getA();
        }
        this.text = nop.apply(this.text);
        this.saveOp(sid, nop);
        ArrayList<String> del = new ArrayList<String>();
        for (Map.Entry<String, Session> entry: store.entrySet()) {
            String id = entry.getKey();
            Session session = entry.getValue();
            if (!session.isOpen()) {
            	del.add(id);
                continue;	
            }
            if (!id.equals(sid)) session.getRemote().sendString(getResponse(sid, nop.getActionsList()));
            else session.getRemote().sendString(getResponse(sid, new LinkedList()), null);
        }
        for (String d: del) {
            if(store.containsKey(d)) store.remove(d);
            if(lastOp.containsKey(d)) lastOp.remove(d);
        }
    }
}
