package org.stampede.config;

import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.config.location.IConfigLocation;

public class ConfigFacade {
	
	IConfigDeserializer deserializer;
	IConfigLocation location;
	
	public ConfigFacade(Deserializer deserialiser, Location location, String path) throws InstantiationException, IllegalAccessException {
		this.deserializer = deserialiser.getInstance();
		this.location =  location.getInstance();
		this.location.watch(path);
	}

	public Config get(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
 