package org.stampede;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.socket.AbstractSocket;
import org.stampede.socket.GrizzlySocket;
import org.stampede.socket.JavaSocket;
import org.stampede.socket.JeroSocket;
import org.stampede.socket.NanoSocket;
import org.stampede.socket.NettySocket;


public class Stampede implements AutoCloseable {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected String SOCKET_IMPLEMENTATION = System.getProperty("stampede.socket","java");
    
    AbstractSocket socket;
    
    private AbstractSocket getClientSocket() throws IOException {
        String socketName = SOCKET_IMPLEMENTATION;
        if(socket != null) {
            return socket;
        }
        logger.info("Instantiating a socket with a " + SOCKET_IMPLEMENTATION + " implementation");
        try {
            switch(SOCKET_IMPLEMENTATION.toLowerCase()) {
                case "java":
                    socketName = JavaSocket.class.getCanonicalName();
                    break;
                case "grizzly":
                    socketName = GrizzlySocket.class.getCanonicalName();
                    break;
                case "nano":
                    socketName = NanoSocket.class.getCanonicalName();
                    break;
                case "netty":
                    socketName = NettySocket.class.getCanonicalName();
                    break;
                case "zeromq":
                case "jeromq":
                	socketName = JeroSocket.class.getCanonicalName();
                    break;
            }
            
            Constructor<?> socketConstructor = Class.forName(socketName)
                                                       .getDeclaredConstructor();
            socket = (AbstractSocket) socketConstructor.newInstance();
            return socket;
        } catch (Exception e) {
        	logger.error("Couldn't instantiate " + SOCKET_IMPLEMENTATION + " because " + e.getMessage()); 
            throw new IOException("Couldn't instantiate " + SOCKET_IMPLEMENTATION, e);
        }
    }

    @Override
    public void close() throws Exception {
        getClientSocket().stop();
    }
}
