package org.stampede.socket;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Controller {
	
    @Path("/{role}")
    @GET
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    public Response sayHi(@PathParam("role") String role) {
    	/*
    	for(Youngling young : Barn.all()) {
    		young.getState(role);
    	} //not yet
    	*/
        return Response.ok().build();
    }
    
    
}