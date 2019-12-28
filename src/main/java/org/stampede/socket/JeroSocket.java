package org.stampede.socket;

import java.io.IOException;
import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class JeroSocket extends AbstractSocket {

	private ZContext ctx;
	private ZMQ.Socket stream;

	public JeroSocket() throws IOException, InterruptedException {
		ctx = new ZContext();
		stream = ctx.createSocket(SocketType.STREAM);
		stream.bind("tcp://" + HOST + ":" + PORT);
	}

	@Override
	public boolean serve() throws IOException {
		// Get HTTP request
		byte[] handle = stream.recv();
		byte[] request = stream.recv(); // TODO: use this
		while (request.length == 0 || Arrays.equals(request, handle)) {
			request = stream.recv();
		}
		getPath(request); // TODO: do something with the path

		// Send 200, I'm okay
		stream.sendMore(handle);
		stream.send(OK, ZMQ.DONTWAIT);

		// Close connection to browser
		stream.sendMore(handle);
		stream.send("", ZMQ.DONTWAIT);
		return true;
	}

	@Override
	protected void close() throws IOException {
		ctx.close();
	}
}
