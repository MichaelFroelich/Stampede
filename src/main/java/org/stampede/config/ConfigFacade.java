package org.stampede.config;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.config.location.IConfigLocation;

/*
 * Exists as a root of the Config tree
 */
public class ConfigFacade extends Config {
	
	IConfigDeserializer deserializer;
	IConfigLocation location;
	ArrayList<Config> leafs = new ArrayList<Config>();
	
	public ConfigFacade(String path) {
		if(path.startsWith("git")) {
			
		}
	}
	
	public ConfigFacade(Deserializer deserialiser, Location location, String path) throws InstantiationException, IllegalAccessException {
		this.deserializer = deserialiser.getInstance();
		this.location = location.getInstance();
		this.location.watch(path);
	}
	
	/**
	 * Watching loop
	 */
	private void watch() {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ses.scheduleAtFixedRate(new Runnable() {
		    @Override
		    public void run() {
		    }
		}, 0, 1, TimeUnit.SECONDS);  // execute every second
	}

	protected void addLeaf(Config config) { 
		this.leafs.add(config);
	}
}
 