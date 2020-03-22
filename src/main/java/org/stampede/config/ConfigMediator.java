package org.stampede.config;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.zookeeper.client.ConnectStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.config.deserializer.ErroredDeserializer;
import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.config.location.ErroredLocation;
import org.stampede.config.location.FileEvent;
import org.stampede.config.location.IConfigLocation;

/*
 * Exists as a root of the Config tree
 */
public class ConfigMediator extends Config implements AutoCloseable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private IConfigDeserializer deserializer;
	private IConfigLocation location;
	private ArrayList<Config> leafs = new ArrayList<Config>();
	private ScheduledExecutorService ses;
	private Path localPath;
	private static final Pattern KEEPERPATTERN = Pattern.compile("\\.[a-z]*keep$");
	
	/**
	 * 
	 * @param path assuming it's a complete uri including a protocol
	 */
	public ConfigMediator(String path) {
		this(null, path);
	}

	/**
	 * @param deserialiser to use as a default if files do not have a recognised
	 *                     extension
	 * @param location     to the configurations
	 * @param path         to the configurations
	 */
	public ConfigMediator(Location location, String path) {
		this(Deserializer.Properties, location, path);
	}

	/**
	 * @param deserialiser to use as a default if files do not have a recognised
	 *                     extension
	 * @param location     to the configurations
	 * @param path         to the configurations
	 */
	public ConfigMediator(Deserializer deserialiser, Location location, String path) {
		this.path = "";
		if (location == null) {
			location = findLocation(path);
		}
		try {
			this.deserializer = deserialiser.getInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			this.deserializer = new ErroredDeserializer();
			logger.error("Failed to instanstiate deserializer because" + e1.getMessage());
		}
		try {
			this.location = location.getInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			this.location = new ErroredLocation();
			logger.error("Failed to instanstiate location because" + e1.getMessage());
		}

		this.localPath = Paths.get(path);
		List<Path> files = this.location.register(this);
		for (Path p : files) {
			addFile(p);
		}
		if (ses == null)
			ses = Executors.newScheduledThreadPool(1);

		ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				watch();
			}
		}, 0, 1, TimeUnit.SECONDS); // execute every second
	}

	private void addFile(Path p) {
		if (p.toFile().isDirectory()) {
			Boolean isLeaf = true;
			File file = p.toFile();
			for(File f : file.listFiles()) {
				if(f.isDirectory()) { 
					isLeaf = false;
					break;
				}
			}
			if(isLeaf) {
				Config newconfig = new Config(p, this, localPath.relativize(p).toString());
				addLeaf(newconfig);
			}
		} else {
			try (Reader stream = Files.newBufferedReader(p, Charset.defaultCharset())) {
				// Make the root of this config relative to the root of all configs
				if (!KEEPERPATTERN.matcher(p.toString()).find()) {
					String root = stripExtension(localPath.relativize(p).toString());
					Config newconfig = new Config(p, this, root);
					String extension = getExtension(root).toLowerCase();
					IConfigDeserializer implementation = deserializer;
					try {
						switch (extension){
							case "properties":
							case "property":
								implementation = Deserializer.Properties.getInstance();
							case "json":
							case "js":
								implementation = Deserializer.Json.getInstance();
							case "yaml":
							case "yml":
								implementation = Deserializer.Yaml.getInstance();
							case "xml":
								implementation = Deserializer.XML.getInstance();
							case "csv":
								implementation = Deserializer.CSV.getInstance();
						}
					} catch (InstantiationException | IllegalAccessException e) {
						logger.error("Failed to instantiate serializer assumed by an extension of " + extension
								+ " because " + e.getMessage());
					}
					implementation.load(stream, newconfig);
				}
			} catch (IOException e) {
				logger.error("Something went wrong with " + p + " : " + e.getMessage());
			}
		}
	}

	/***
	 * TODO: fix this
	 * @param p
	 * @return
	 */
	private Location findLocation(String p) {
		if (p.startsWith("git") || p.startsWith("http") || p.startsWith("https") || p.endsWith("git")) {
			return Location.Git;
		} else if (p.startsWith("ftp") || p.contains("@")) {
			return Location.FTP;
		} else {
			ConnectStringParser parser = null;
			try {
				parser = new ConnectStringParser(p);
			} catch (IllegalArgumentException dontcare) {
			}
			if (parser != null && !parser.getServerAddresses().isEmpty())
				return Location.ZooKeeper;
		}
		return Location.Local;
	}

	/**
	 * Watching loop
	 */
	private void watch() {

		for (Entry<Path, FileEvent> p : location.poll().entrySet()) {
			try {
				String key = stripExtension(localPath.relativize(p.getKey()).toString());
				switch (p.getValue()){
					case DELETE:
						this.get(key).delete();
						break;
					case CREATE:
						addFile(p.getKey());
						break;
					case MODIFY:
						this.get(key).delete();
						addFile(p.getKey());
						break;
					default:
						// bitch and moan
				}
			} catch (Exception e) {
				logger.error(
						"Failed to load config file: " + p.getKey().toAbsolutePath() + " because " + e.getMessage());
			}
		}
	}

	protected void addLeaf(Config config) {
		this.leafs.add(config);
	}
	
	public Config[] getLeaves() {
		return this.leafs.toArray(new Config[leafs.size()]);
	}

	public Path getLocalPath() {
		return this.localPath;
	}

	@Override
	public void close() throws Exception {
		ses.shutdownNow();
	}

	static String getExtension(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;
		return str.substring(1, pos);
	}

	static String stripExtension(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;
		return str.substring(0, pos);
	}

	public Location getLocation() {
		return null;
	}
}
