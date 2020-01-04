package org.stampede.model;

import org.stampede.config.Config;

public class Role {
	
	String name;
	
	Status state;
	
	Config config;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getState() {
		return state;
	}

	public void setState(Status state) {
		this.state = state;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
