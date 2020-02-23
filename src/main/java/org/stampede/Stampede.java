package org.stampede;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.config.Deserializer;
import org.stampede.config.Location;
import org.stampede.config.Socket;
import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.model.Role;
import org.stampede.socket.AbstractSocket;
import static org.stampede.StampedeConfig.*;

public class Stampede implements AutoCloseable, Watcher {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String zookeeperConnectionString = null;
	private AbstractSocket socket;
	private boolean started;
	private Barn barn;
	private Config initConfig;
	private EmbeddedZookeeper zookeeper;
	private CuratorFramework curator;

	/**
	 * Create an instance of Stampede Assumes all configs are already loaded into
	 * the system properties
	 */
	public Stampede() {
		this(null);
	}

	/**
	 * Create an instance of Stampede
	 * 
	 * @param path to an initial configuration directory
	 */
	public Stampede(String path) {
		if (path != null)
			init(path);
		barn = new Barn(this);
		zookeeper = (EmbeddedZookeeper) startEmbeddedZookeeper();
		curator = startZookeeperClient();
	}

	protected String getConnectionString() {
		if (zookeeperConnectionString == null) {
			String propertyConnectionString = System.getProperty("zookeeper.connectionString", "");
			StringBuilder otherConnectionString = new StringBuilder(propertyConnectionString);
			if (!propertyConnectionString.isEmpty()
					&& (propertyConnectionString.charAt(propertyConnectionString.length() - 1)) != ',') {
				otherConnectionString.append(',');
			}
			int i = 0;
			String someServer = System.getProperty("zookeeper.server." + Integer.toString(i), null);
			while (someServer != null) {
				otherConnectionString.append(someServer).append(',');
				someServer = System.getProperty("zookeeper.server." + Integer.toString(i++), null);
			}
			zookeeperConnectionString = otherConnectionString.toString();
		}
		return zookeeperConnectionString;
	}

	private boolean init(String path) {
		boolean success = true;
		try {
			String extension = Util.getFinalLabel(path.toString());
			switch (extension){
				case "properties":
					IConfigDeserializer deserialiser = Deserializer.Properties.getInstance();
					Properties prop = new Properties();
					Reader targetReader = Files.newBufferedReader(Paths.get(path), Charset.defaultCharset());
					initConfig = deserialiser.load(targetReader, new Config());
					for (Entry<String, Object> property : initConfig.flatten().entrySet()) {
						System.setProperty(property.getKey(), property.getValue().toString());
					}
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

	private CuratorFramework startZookeeperClient() {
		String connectionString = StampedeConfig.getConnectionString();
		if (connectionString == null) {
			connectionString = Util.scanZookeeper();
		}
		int maxRetryTime = StampedeConfig.getZookeeperMaxRetryTime();
		int maxRetryCount = StampedeConfig.getZookeeperMaxRetryCount();
		int tickTime = StampedeConfig.getZookeeperTickTime() - 1;
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, maxRetryCount);
		CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(connectionString)
				.retryPolicy(retryPolicy).connectionTimeoutMs(maxRetryTime).sessionTimeoutMs(tickTime).build();
		curator.getConnectionStateListenable();
		curator.start();
		return curator;
	}

	private ZooKeeperServerMain startEmbeddedZookeeper() {
		if (true) // some precondition
			zookeeper = new EmbeddedZookeeper();
		return zookeeper;
	}

	public boolean isStarted() {
		if (!started)
			started = true;
		return started;
	}

	private CuratorFramework getCuratorConnection() {
		if (curator == null)
			curator = startZookeeperClient();
		return curator;
	}

	private AbstractSocket getClientSocket() throws IOException {
		if (socket != null) {
			return socket;
		}
		logger.info("Instantiating a socket with a " + getSocketImplementation() + " implementation");
		try {
			if (getSocketImplementation() == null) {
				return Socket.getAnyInstance();
			}

			switch (getSocketImplementation().toLowerCase()){
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
			throw new Exception("Unknown implementation, perhaps it's unimplemented?");
		} catch (Exception e) {
			logger.error("Couldn't instantiate " + getSocketImplementation() + " because " + e.getMessage());
			throw new IOException("Couldn't instantiate " + getSocketImplementation(), e);
		}
	}

	@Override
	public void close() throws Exception {
		if (socket != null)
			socket.stop();
		if (zookeeper != null)
			zookeeper.shutdown();
		if (curator != null)
			curator.close();
	}

	public Barn getBarn() {
		return barn;
	}

	public void createZookeeperStore(Config config) throws Exception {
		try {
			byte[] data = serialize(config.getResult());
			createZookeeperStore(config.getPath(), data);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void createZookeeperStore(String path, Object result) throws Exception {
		byte[] data = serialize(result);
		curator.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT)
		.forPath(path, data);
		
		Stat stat = curator.getZookeeperClient().getZooKeeper().exists(path, this);
		
		if(stat != null)
			logger.debug("ZK stat: " + stat.toString());
		else
			logger.warn("null ZK node for " + path);
	}
	
	private byte[] serialize(Object result) throws IOException {
		byte[] data = null;
		if(result instanceof Path && !((Path)result).toFile().isDirectory()) {
			data = Files.readAllBytes(((Path)result));
		} else {
			data = Util.binarySerialize(result);
		}
		return data;
	}

	public void leaderElection(Role[] roles) {
		
		
	}

	@Override
	public void process(WatchedEvent event) {
		for(Youngling youngun : this.barn.all()) {
			for(ConfigMediator cm : youngun.getConfigs()) {
				if(cm.getLocation() == Location.ZooKeeper) {
			}
		}
		//TODO:
	}
	
	CuratorFramework getCuratorClient(){
		return curator;
	}
}
