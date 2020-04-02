package org.stampede;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.model.Role;
import org.stampede.model.Status;

public abstract class Youngling {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected List<ConfigMediator> configs;
	protected List<RoleListener> listeners;
	protected HashMap<String, Role> roles;
	protected Stampede stampede;
	
	public Youngling(Stampede stampede) {
		this.stampede = stampede;
		roles = new HashMap<String, Role>();
		configs = new ArrayList<ConfigMediator>();
		listeners = new ArrayList<RoleListener>();
	}
	
	/**
	 * Register configurations and their corresponding roles or a regex referring to their roles
	 * @param configDetails that describe the location and type of the configs you're looking for
	 * @param rolesMatcher a string that eventually becomes a PathMatcher @see {@link java.nio.file.FileSystem#getPathMatcher(String)}
	 */
	public void registerConfig(ConfigMediator configDetails, String... rolesMatcher) {
		
		configs.add(configDetails);
		Config[] leaves = configDetails.getLeaves();
		for (String toMatch : rolesMatcher) {
			if (!toMatch.startsWith("glob") && !toMatch.startsWith("regex")) {
				if (Util.isRegex(toMatch)) {
					toMatch = "regex:" + toMatch;
				} else {
					toMatch = "glob:" + toMatch;
				}
			}
			for (Config c : leaves) {
				Path p = ((Path) c.getResult());
				try {
					PathMatcher pm = p.getFileSystem().getPathMatcher(toMatch);
					if (pm.matches(p)) {
						roles.put(c.getPath(), new Role(c));
						
					}
				} catch (IllegalArgumentException e) {
					logger.error("Bad pattern of " + toMatch + " because " + e.getMessage());
				}
			}
		}
	}
	
	abstract protected void advertise();
	
	abstract protected void unadvertise();

	public Config getConfig(String nextNode) {
		for(Config c : configs) {
			if(c.get(nextNode) != null) {
				return c.get(nextNode);
			}
		}
		return null;
	}

	public Status getState(String role) {
		Status toreturn =  Status.DEAD;
		Role r = roles.get(role);
		if(r != null) {
			toreturn = r.getState();
		}
		return toreturn;
	}

	public Role getRole(String string) {
		return roles.get(string);
	}

	public Role[] getRoles() {
		return roles.values().toArray(new Role[roles.size()]);
	}
	
	public ConfigMediator[] getConfigs() {
		return configs.toArray(new ConfigMediator[configs.size()]);
	}

	public boolean registerListener(RoleListener roleListener) {
		return listeners.add(roleListener);
	}
	
}
