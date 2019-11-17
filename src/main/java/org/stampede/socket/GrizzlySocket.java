package org.stampede.socket;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class GrizzlySocket extends AbstractSocket {

    public GrizzlySocket(int port) {
        super(port);
    }

    @Override
    public void start() throws IOException, InterruptedException {

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

        ResourceConfig config = new ResourceConfig(org.stampede.HealthResource.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);

        // Minimise the number of threads running
        ThreadPoolConfig threadConfig = ThreadPoolConfig.defaultConfig().setPoolName(
                "workerpool").setCorePoolSize(1).setMaxPoolSize(5);

        // assign the thread pool
        NetworkListener listener = server.getListeners().iterator().next();
        listener.getTransport().setWorkerThreadPoolConfig(threadConfig);

        server.start();
        super.start();
    }

    @Override
    public void close() throws IOException {
    }

}
