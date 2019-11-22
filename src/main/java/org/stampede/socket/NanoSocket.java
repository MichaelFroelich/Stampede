package org.stampede.socket;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class NanoSocket extends AbstractSocket {

    Server server;

    class Server extends NanoHTTPD {

        public Server() throws IOException {
            super(port);

            if (SSL) {
                makeSecure(
                        NanoHTTPD.makeSSLSocketFactory(
                                KEYSTORE_FILE, KEYSTORE_PASSWORD.toCharArray()),
                        null);
            }
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }

        @Override
        public Response serve(IHTTPSession session) {
            return newFixedLengthResponse("Hello world");
        }
    }

    public NanoSocket(int port) {
        super(port);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        server = new Server();
        super.start();
    }

    @Override
    public void close() throws IOException {
        server.stop();
    }

}
