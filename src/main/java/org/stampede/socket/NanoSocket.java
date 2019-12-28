package org.stampede.socket;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class NanoSocket extends AbstractSocket {

	Server server;

	static final Response OK = NanoHTTPD.newFixedLengthResponse(Status.OK, null, null);
	static final Response NOTOK = NanoHTTPD.newFixedLengthResponse(Status.SERVICE_UNAVAILABLE, null, null);

	class Server extends NanoHTTPD {

		public Server() throws IOException {
			super(PORT);

			if (SSL) {
				makeSecure(NanoHTTPD.makeSSLSocketFactory(KEYSTORE_FILE, KEYSTORE_PASSWORD.toCharArray()), null);
			}
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		}

		@Override
		public Response serve(IHTTPSession session) {
			String path = null;
			if (session.getUri().length() > 1)
				path = session.getUri().substring(1);
			return OK;
		}
	}

	public NanoSocket() throws IOException, InterruptedException {
		server = new Server();
	}

	@Override
	protected void close() throws IOException {
		server.stop();
	}

}
