package org.stampede.config;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.server.UnimplementedRequestProcessor;

public class Config {

	private Object result;

	ConcurrentHashMap<String, Config> keyValuePairs;
	
	public Config() {
		keyValuePairs = null;
		result = null;
	}
	
	public Config(Object result, ConcurrentHashMap<String, Config> keyValuePairs) {
		if(result instanceof File) {
			//record stuff necessary for watching
		}
		
		this.keyValuePairs = keyValuePairs;
		this.result = result;
	}
	
	public Object getResult() {
		return result;
	}
	
	public final Config get(String root) {
		return keyValuePairs.get(root);
	}
	
	public HashMap<String, String> flatten() {
		throw new RuntimeException("Not implemented");
	}
}
