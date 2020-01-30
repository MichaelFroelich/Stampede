package org.stampede;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.config.Config;
import org.stampede.config.Deserializer;
import org.stampede.config.Socket;
import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.socket.AbstractSocket;

public class Stampede implements AutoCloseable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected String SOCKET_IMPLEMENTATION = System.getProperty("stampede.socket", null);

	protected String APPLICATION_NAME = System.getProperty("stampede.name", null);

	private AbstractSocket socket;

	private boolean started;

	private Barn barn;
	
	private Config initConfig;

	public Stampede() {
		this(null);
	}


	/**
	 * @param path to an initial configuration directory
	 */
	public Stampede(Path path) {
		if (init(path)) {
			barn = new Barn(this);
		} else
			logger.error("Bad error");

	}

	private boolean init(Path path) {
		boolean success = true;
		try {
			String extension = Util.getFinalLabel(path.toString());
			switch (extension){
				case "properties":
					IConfigDeserializer deserialiser = Deserializer.Properties.getInstance();

					Properties prop = new Properties();
					Reader targetReader = Files.newBufferedReader(path, Charset.defaultCharset());
					
					Config config = deserialiser.load(targetReader);
					/*
					for(Entry<String, String> property : config.flatten().entrySet()) {
						System.setProperty(property.getKey(), property.getValue());
					}
					*/
					prop.load(targetReader);
					System.setProperties(prop);
					targetReader.close();
					break;

				case "json":
					break;

				case "yaml":
					break;

				case "xml": // you might be this evil...
					break;

				case "csv":
					break;

				default:
					success = false;
			}
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

	public boolean isStarted() {
		if (!started)
			started = true;
		return started;
	}

	private AbstractSocket getClientSocket() throws IOException {
		if (socket != null) {
			return socket;
		}
		logger.info("Instantiating a socket with a " + SOCKET_IMPLEMENTATION + " implementation");
		try {
			if (SOCKET_IMPLEMENTATION == null) {
				return Socket.getAnyInstance();
			}
			
			switch (SOCKET_IMPLEMENTATION.toLowerCase()){
				case "grizzly":
				case "glassfish":
					return Socket.Grizzly.getInstance();
				case "nano":
				case "nanohttpd":
					return Socket.Nano.getInstance();
				case "netty":
					return Socket.Netty.getInstance();
				case "zeromq":
				case "jeromq":
					return Socket.JeroMq.getInstance();
				case "java":
				case "default":
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

	public Barn getBarn() {
		return barn;
	}
}
