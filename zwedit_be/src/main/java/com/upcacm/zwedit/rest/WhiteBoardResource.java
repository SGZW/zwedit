package com.upcacm.zwedit.rest;
  
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  
import javax.ws.rs.core.MediaType;  
  
@Path("whiteboards")
public class WhiteBoardResource {

    @GET
    @Path("{whiteboardurl}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("whiteboardurl") String url) throws Exception {
        return "whiteboardurl! " + url;
    }
}
