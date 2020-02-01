package org.stampede.socket;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.stampede.Barn;
import org.stampede.Youngling;

@Path("/")
public class Controller {
	
    @Path("/{role}")
    @GET
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    public Response sayHi(@PathParam("role") String role) {
    	
    	for(Youngling young : Barn.all()) {
    		young.getState(role);
    	}
    	
        return Response.ok().build();
    }
    
    
}