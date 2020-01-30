package org.stampede.config;

import java.util.ArrayList;
import java.util.List;

import org.stampede.Util;
import org.stampede.config.deserializer.IConfigDeserializer;
import org.stampede.config.deserializer.json.*;
import org.stampede.config.deserializer.csv.*;
import org.stampede.config.deserializer.yaml.*;

public enum Deserializer {

        Json(null),
            JacksonJson(Json, null, JacksonJSONDeserializer.class),
            Gson(Json, null, GsonDeserializer.class),
        Yaml(null),
            JacksonYaml(Yaml, null, JacksonYAMLDeserializer.class),
            SnakeYaml(Yaml, null, SnakeYAMLDeserializer.class),
            YAMLBeans(Yaml, null, YAMLBeansDeserializer.class),
        XML(null),
        	JacksonXML(XML),
        	SAX(XML),
        Properties(null, null, null),
        CSV(null),
        	StampedeCSV(CSV, null, StampedeCSVDeserializer.class),
        	JacksonCSV(CSV, null, JacksonCSVDeserializer.class),
        	CommonCSV(CSV, null, CommonCSVDeserializer.class);
	
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
	
	IConfigDeserializer instance = null;
	
	public IConfigDeserializer getInstance() throws InstantiationException, IllegalAccessException {
		if(instance == null) {
			Deserializer[] children = children();
			if(children.length != 0) {
				for(Deserializer child : children) {
					if(child.isActive())
						instance = (IConfigDeserializer)child.getInstance();
				}
			} else {
				instance = (IConfigDeserializer)implementation.newInstance();
			}
		}
		return instance;
	}
}
