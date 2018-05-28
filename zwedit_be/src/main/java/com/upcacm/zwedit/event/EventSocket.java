package com.upcacm.zwedit.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.upcacm.zwedit.controller.ZweditController;
import com.upcacm.zwedit.controller.EditRoom;
import com.upcacm.zwedit.util.JsonUtil;
import com.upcacm.zwedit.util.Pair;
import com.upcacm.zwedit.ot.TextOperation;

@WebSocket
public class EventSocket {
    
    private final Logger logger = LoggerFactory.getLogger(EventSocket.class);
    
    
    public static String generateNewResponse(Integer revision, String text) {
        return "";
    }    

    @OnWebSocketConnect
    public void onConnect(Session session) { 
        String roomUrl = session.getUpgradeRequest().getParameterMap().get("roomUrl").get(0);
        String sid = session.getUpgradeRequest().getParameterMap().get("sid").get(0);
        EditRoom e = ZweditController.getZweditController().getEditRoom(roomUrl);
        Pair<Integer, String> p = e.getNewResponse();
        String res = generateNewResponse(p.getA(), p.getB());
        session.getRemote().sendString(res, null);
        e.addSession(sid, session);
    }
    
    @OnWebSocketMessage
    public void onText(Session session, String message) {
        String roomUrl = session.getUpgradeRequest().getParameterMap().get("roomUrl").get(0);
        String sid = session.getUpgradeRequest().getParameterMap().get("sid").get(0);
        EditRoom e = ZweditController.getZweditController().getEditRoom(roomUrl);
        Pair<Integer, TextOperation> p = JsonUtil.load(message);
        e.process(sid, p.getA().intValue(), p.getB());
    }
    
    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
	    logger.error("websocket error: ", cause);
    }
    
    @OnWebSocketClose
    public void onClose(Session session, int closeCode, String closeReason) {}                       
}
