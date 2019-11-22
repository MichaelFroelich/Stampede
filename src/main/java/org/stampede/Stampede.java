package org.stampede;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.stampede.socket.AbstractSocket;
import org.stampede.socket.GrizzlySocket;
import org.stampede.socket.JavaSocket;
import org.stampede.socket.NanoSocket;
import org.stampede.socket.NettySocket;


public class Stampede implements AutoCloseable {
    
    protected String SOCKET_IMPLEMENTATION = System.getProperty("stampede.socket","java");
    
    AbstractSocket socket;
    
    private AbstractSocket getClientSocket() throws IOException {
        String socketName = SOCKET_IMPLEMENTATION;
        if(socket != null) {
            return socket;
        }
        
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
            }
            
            Constructor<?> socketConstructor = Class.forName(socketName)
                                                       .getDeclaredConstructor(int.class);
            socket = (AbstractSocket) socketConstructor.newInstance(1024);
            return socket;
        } catch (Exception e) {
            throw new IOException("Couldn't instantiate " + SOCKET_IMPLEMENTATION, e);
        }
    }

    @Override
    public void close() throws Exception {
        getClientSocket().close();
    }
}
