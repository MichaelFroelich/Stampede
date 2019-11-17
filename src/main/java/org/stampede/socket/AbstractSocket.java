package org.stampede.socket;

import java.io.IOException;

public abstract class AbstractSocket {
    
    //Do not make these static as we'd like them initiated just before instantiation 
    protected boolean SSL = System.getProperty("ssl") != null;
    protected int PORT = Integer.parseInt(System.getProperty("port", "1024"));
    protected String KEYSTORE_FILE = System.getProperty("javax.net.ssl.keyStore",".keystore");
    protected String KEYSTORE_TYPE = System.getProperty("javax.net.ssl.keyStoreType","JKS");
    protected String KEYSTORE_PASSWORD = System.getProperty("javax.net.ssl.keyStorePassword","changeit");
    
    protected int port;
    
    public AbstractSocket(int port) {
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        new ServerRunner(this).run();
    }
    
    public abstract void close() throws IOException;
    
    public boolean serve() throws IOException {
        return false;
    }
}
