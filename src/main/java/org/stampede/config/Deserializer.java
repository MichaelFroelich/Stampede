package org.stampede.config;

import java.util.ArrayList;
import java.util.List;

import org.stampede.Util;
import org.stampede.config.deserializer.IConfigDeserializer;

public enum Deserializer {

        Json(null),
            FasterXML(Json),
            Gson(Json),
        Yaml(null),
            JacksonYaml(Yaml),
            SnakeYaml(Yaml),
            YamlBeans(Yaml),
        XML(null),
        Properties(null),
        CSV(null),
        	FasterCSV(CSV);
	
	private Deserializer parent = null;
	
	private String checkingLibrary;
	
	private Class<? extends IConfigDeserializer> implementation;

	protected List<Deserializer> children = new ArrayList<Deserializer>();
	
	private Deserializer(Deserializer parent) {
		this.implementation = null;
		this.checkingLibrary = null;
	    this.parent = parent;
	    if (this.parent != null) {
	        this.parent.children.add(this);
	    }
	}
	
	private Deserializer(Deserializer parent, String checkingLibrary, Class<? extends IConfigDeserializer> implementation) {
		this.implementation = implementation;
		this.checkingLibrary = checkingLibrary;
	    this.parent = parent;
	    if (this.parent != null) {
	        this.parent.children.add(this);
	    }
	}
	
	private Deserializer[] children() {
	    return children.toArray(new Deserializer[children.size()]);
	}
	
	private boolean isActive() {
		if(checkingLibrary != null)
			return Util.checkClass(checkingLibrary);
		else return false;
	}
	
	IConfigDeserializer getInstance() throws InstantiationException, IllegalAccessException {
		Deserializer[] children = children();
		if(children.length != 0) {
			for(Deserializer child : children) {
				if(child.isActive())
					return (IConfigDeserializer)child.getInstance();
			}
		} else {
			return (IConfigDeserializer)implementation.newInstance();
		}
		return null;
	}
}
