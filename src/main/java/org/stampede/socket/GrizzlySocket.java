package org.stampede.socket;

import java.io.IOException;
import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrizzlySocket extends AbstractSocket {
	
	HttpServer server;
	
	Object mutex;
	 
    public GrizzlySocket() throws IOException, InterruptedException {
    	mutex = new Object();
        URI baseUri = UriBuilder.fromUri("http://" + HOST + "/").port(PORT).build();

        ResourceConfig config = new ResourceConfig(Controller.class);

        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);

        // Minimise the number of threads running
        ThreadPoolConfig threadConfig = ThreadPoolConfig.defaultConfig().setPoolName(
                "workerpool").setCorePoolSize(1).setMaxPoolSize(5);

        // assign the thread pool
        NetworkListener listener = server.getListeners().iterator().next();
        listener.getTransport().setWorkerThreadPoolConfig(threadConfig);
        synchronized (mutex) {
            server.start();
		}
    }
    
    @Override
    protected void close() throws IOException {
    	synchronized (mutex) {
        	server.shutdownNow();
		}
    }

}
