package org.stampede.config;

import java.util.ArrayList;
import java.util.List;

import org.stampede.Util;
import org.stampede.socket.*;

public enum Socket {

	Netty("io.netty.channel.ChannelInboundHandlerAdapter", NettySocket.class),
	JeroMq("org.zeromq.ZMQ", JeroSocket.class),
	Nano("fi.iki.elonen.NanoHTTPD", NanoSocket.class),
	Java(null, JavaSocket.class),
	Grizzly("org.glassfish.grizzly.http.server.HttpServer", GrizzlySocket.class);

	private String checkingLibrary;

	private Class<? extends AbstractSocket> implementation;

	protected List<Socket> children = new ArrayList<Socket>();

	private Socket(String checkingLibrary, Class<? extends AbstractSocket> implementation) {
		this.implementation = implementation;
		this.checkingLibrary = checkingLibrary;
	}

	public Socket[] children() {
		return children.toArray(new Socket[children.size()]);
	}

	public boolean isActive() {
		if (checkingLibrary != null)
			return Util.checkClass(checkingLibrary);
		else
			return false;
	}

	public AbstractSocket getInstance() throws InstantiationException, IllegalAccessException {
		return implementation.newInstance();
	}
	
	public static AbstractSocket getAnyInstance() throws InstantiationException, IllegalAccessException {
		for(Socket value : Socket.values()) {
			if(value != Socket.Java && value.isActive()) {
				return value.getInstance();
			}
		}
		return Socket.Java.getInstance();
	}
}
