package org.stampede.config;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Config {

	private Object result;
	
	private Config parent;

	ConcurrentHashMap<String, Config> keyValuePairs;
	
	public Config() {
		keyValuePairs = null;
		result = null;
	}
	
	public Config(Object result, ConcurrentHashMap<String, Config> keyValuePairs, Config parent) {
		this(result, keyValuePairs);
		this.setParent(parent);
	}
	
	public Config(Object result, ConcurrentHashMap<String, Config> keyValuePairs) {
		if(result instanceof File) {
			Config root = getRoot();
			if(root instanceof ConfigFacade) {
				ConfigFacade configFacade = (ConfigFacade) root;
				configFacade.addLeaf(this);
			}
			//record stuff necessary for watching and add to node list
		}
		
		this.keyValuePairs = keyValuePairs;
		this.result = result;
	}
	
	private Config getRoot() {
		Config pointer = this.parent;
		while(pointer != null && pointer.getParent() != null) {
			pointer = pointer.getParent();
		}
		return pointer;
	}
	
	public Object getResult() {
		return result;
	}
	
	public Config getParent() {
		return parent;
	}

	public void setParent(Config parent) {
		this.parent = parent;
	}

	public final Config get(String root) {
		return keyValuePairs.get(root);
	}
	
	public HashMap<String, String> flatten() {
		throw new RuntimeException("Not implemented");
	}
}
