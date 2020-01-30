package org.stampede.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.config.location.IConfigLocation;

public class ConfigFacade {
	
	IConfigDeserializer deserializer;
	IConfigLocation location;
	Config config;
	
	public ConfigFacade() {
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
		        // do some work
		    }
		}, 0, 1, TimeUnit.SECONDS);  // execute every second
	}

	public Config get(String string) {
		return config.get(string);
	}

}
 