package org.stampede.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Config {

	private Object result;
	private Config parent;
	private String path;
	protected HashMap<String, Config> keyValuePairs;
	private static final Pattern FILESPLITTER = Pattern.compile("\\.|/|\\\\");

	public Config() {
		keyValuePairs = new HashMap<String, Config>();
		result = null;
	}

	public Config(Object result, Config parent, String key) {
		keyValuePairs = new HashMap<String, Config>();
		this.result = result;
		this.setParent(parent);
		parent.add(key, this);
	}

	public Config getRoot() {
		Config pointer = this.parent;
		while (pointer != null && pointer.getParent() != null) {
			pointer = pointer.getParent();
		}
		return pointer;
	}

	public Object getResult() {
		return result;
	}

	protected void setResult(Object result) {
		this.result = result;
	}

	public Config getParent() {
		return parent;
	}

	public void setParent(Config parent) {
		this.parent = parent;
	}

	public final Config get(String root) {
		if (root.contains(".") || root.contains("/") || root.contains("\\")) {
			String[] substrings = FILESPLITTER.split(root);
			LinkedList<String> keysegments = new LinkedList<String>(Arrays.asList(substrings));
			return this.get(keysegments);
		} else {
			return keyValuePairs.get(root);
		}
	}

	private Config get(LinkedList<String> keysegments) {
		Config current = this;
		while (keysegments.size() > 0) {
			current = current.get(keysegments.pop());
		}
		return current;
	}

	public HashMap<String, Object> flatten() {
		HashMap<String, Object> toreturn = new HashMap<String, Object>();
		for(Entry<String, Config> e : keyValuePairs.entrySet()) {
			if(e.getValue().getResult() != null) {
				toreturn.put(e.getKey(), e.getValue());
			} else {
				toreturn.putAll(e.getValue().flatten());
			}
		}
		return toreturn;
	}

	public void add(String key, Config config) {
		Config finalResult;
		String[] substrings = FILESPLITTER.split(key);
		if (substrings.length > 1) {
			LinkedList<String> keysegments = new LinkedList<String>(Arrays.asList(substrings));
			finalResult = this.add(keysegments.pop(), null, keysegments);
			config.parent = finalResult;
			config.path = substrings[substrings.length - 1];
			finalResult.keyValuePairs.put(substrings[substrings.length - 1], config);
		} else {
			config.path = key;
			this.keyValuePairs.put(key, config);
		}
	}

	protected Config add(String key, Config config, LinkedList<String> keysegments) {
		if (keysegments.size() > 0) {
			if (!keyValuePairs.containsKey(key)) {
				config = new Config();
				config.parent = this;
				config.path = key;
				keyValuePairs.put(key, config);
			} else {
				config = keyValuePairs.get(key);
			}
			return config.add(keysegments.pop(), null, keysegments);
		} else {
			return this;
		}
	}
	
	protected void delete() {
		this.parent.keyValuePairs.remove(this.path);
		this.keyValuePairs.clear();
	}
}
