package org.stampede.socket.grizzly;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Singleton
public class GrizzlyController {

    /**
     * Constructor
     */
    public GrizzlyController() {
    }
    
    @Path("/{path}")
    @GET
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    public Response sayHi(@PathParam("path") String path) {
        return Response.ok().build();
    }
}