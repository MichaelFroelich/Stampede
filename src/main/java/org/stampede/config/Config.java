package org.stampede.config;

import java.util.concurrent.ConcurrentHashMap;

public class Config {

	private Object result;

	ConcurrentHashMap<String, Config> keyValuePairs;
	
	public Config() {
		keyValuePairs = null;
		result = null;
	}
	
	public Config(Object result, ConcurrentHashMap<String, Config> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
		this.result = result;
	}
	
	public final Object getResult() {
		return result;
	}
	
	public final Config get(String root) {
		return keyValuePairs.get(root);
	}
}
