package com.upcacm.zwedit.rest;
  
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  
import javax.ws.rs.core.MediaType;  

import com.upcacm.zwedit.controller.ZweditController;
import com.upcacm.zwedit.controller.EditRoom;
  
@Path("whiteboards")
public class WhiteBoardResource {

    public String formatRes(String status, String msg) {
        return "{\"status\": \"" + status + "\", \"msg\":" + msg + "}"; 
    }
    
    public String addQuote(String msg) {
	return "\"" + msg + "\"";
    }
    
    @GET
    @Path("{whiteboardurl}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getWhiteboard(@PathParam("whiteboardurl") String url) throws Exception {
    	ZweditController c = ZweditController.getZweditController();
        String res = "{\"exist\":";
        if(!c.exists(url)) res += "false }";
	else {
            res += "true, ";
	    EditRoom e = c.getEditRoom(url);
	    String text = addQuote(e.getText());
            String sid = addQuote(e.getNewSessionId());
            res += "\"text\":" + text + ",";
            res += "\"sid\":" + sid + "}";
        }
        return formatRes("success", res);
    }
  
    @POST
    @Path("{whiteboardurl}")
    @Produces(MediaType.TEXT_PLAIN)
    public String createWhiteboard(String body, @PathParam("whiteboardurl") String url) throws Exception {
	ZweditController c = ZweditController.getZweditController();
        if(!c.exists(url)) {
            c.createEditRoom(url);
        }
        return formatRes("success", "true");
    }
}