package org.stampede;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Singleton
public class HealthResource {

    private static final String CURRENT_INSTANCE_NAME = "current";

    private final Logger logger = LoggerFactory.getLogger(HealthResource.class);

    private String currentHostname;

    /**
     * Constructor
     */
    public HealthResource() {
    }

    @GET
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    @Produces({
        MediaType.APPLICATION_JSON
    })
    public Response sayHi() {
        return Response.ok().build();
    }

    @GET
    @Path("/monitor/json")
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    @Produces({
        MediaType.APPLICATION_JSON
    })
    public void getAllApplicationHealthStatuses() {
    }

    @GET
    @Path("/monitor/json/{application}")
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    @Produces({
        MediaType.APPLICATION_JSON
    })
    public void getApplicationInstancesHealthStatuses(@PathParam("application") String application) {
    }

    private void getApplicationInstancesHealthStatuses(String application, boolean renderHtmlView) {

    }

    @GET
    @Path("/monitor/json/{application}/{instance}")
    @Consumes({
        MediaType.APPLICATION_JSON
    })
    @Produces({
        MediaType.APPLICATION_JSON
    })
    public void getHealthStatus(@PathParam("application") String application,
                                @PathParam("instance") String instance) {
    }
}
