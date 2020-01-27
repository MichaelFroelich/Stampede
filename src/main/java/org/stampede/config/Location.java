package org.stampede.config;

import java.util.ArrayList;
import java.util.List;

import org.stampede.Util;
import org.stampede.config.location.IConfigLocation;

public enum Location {

    FTP(null),
    	JSCH(FTP),
    	SSHJ(FTP),
    	CommonVFS(FTP),
    Git(null),
    	JGit(Git),
    	JavaGit(Git),
    Local(null),
    ZooKeeper(null);
	private Location parent = null;
	
	private String checkingLibrary;
	
	private Class<?> implementation;

	protected List<Location> children = new ArrayList<Location>();
	
	private Location(Location parent) {
		this.implementation = null;
		this.checkingLibrary = null;
	    this.parent = parent;
	    if (this.parent != null) {
	        this.parent.children.add(this);
	    }
	}
	
	private Location(Location parent, String checkingLibrary, Class<?> implementation) {
		this.implementation = implementation;
		this.checkingLibrary = checkingLibrary;
	    this.parent = parent;
	    if (this.parent != null) {
	        this.parent.children.add(this);
	    }
	}
	
	public Location[] children() {
	    return children.toArray(new Location[children.size()]);
	}
	
	public boolean isActive() {
		if(checkingLibrary != null)
			return Util.checkClass(checkingLibrary);
		else return true;
	}	
	
	IConfigLocation getInstance() throws InstantiationException, IllegalAccessException {
		Location[] children = children();
		if(children.length != 0) {
			for(Location child : children) {
				if(child.isActive())
					return (IConfigLocation)child.getInstance();
			}
		} else {
			return (IConfigLocation)implementation.newInstance();
		}
		return null;
	}
}
