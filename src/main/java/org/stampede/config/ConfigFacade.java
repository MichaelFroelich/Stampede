package org.stampede.config;

import java.util.HashMap;

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
		this.location =  location.getInstance();
		this.location.watch(path);
	}

	public Config get(String string) {
		return config.get(string);
	}

}
 