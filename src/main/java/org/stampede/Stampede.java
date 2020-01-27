package org.stampede;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.config.Socket;
import org.stampede.socket.AbstractSocket;


public class Stampede implements AutoCloseable {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected String SOCKET_IMPLEMENTATION = System.getProperty("stampede.socket",null);
    
    AbstractSocket socket;
    
    boolean started;
    
    private Barn barn;
    
    public Stampede() {
		barn = new Barn(this);
    }
    
    public boolean isStarted() {
    	if(!started) started = true;
    	return started;
    }
    
    private AbstractSocket getClientSocket() throws IOException {
        if(socket != null) {
            return socket;
        }
        logger.info("Instantiating a socket with a " + SOCKET_IMPLEMENTATION + " implementation");
        try {
        	if(SOCKET_IMPLEMENTATION == null) {
        		return Socket.getAnyInstance();
        	}
        	
            switch(SOCKET_IMPLEMENTATION.toLowerCase()) {

				case "grizzly":
					return Socket.Grizzly.getInstance();
                case "nano":
                	return Socket.Nano.getInstance();
                case "netty":
                	return Socket.Netty.getInstance();
                case "zeromq":
                case "jeromq":
                	return Socket.JeroMq.getInstance();
                case "java":
                    return Socket.Java.getInstance();
            }
            throw new Exception("Unknown implementation perhaps it's unimplemented?");
        } catch (Exception e) {
        	logger.error("Couldn't instantiate " + SOCKET_IMPLEMENTATION + " because " + e.getMessage()); 
            throw new IOException("Couldn't instantiate " + SOCKET_IMPLEMENTATION, e);
        }
    }

    @Override
    public void close() throws Exception {
        getClientSocket().stop();
    }
    
    public Barn getFactory() {
    	return barn;
    }
}
