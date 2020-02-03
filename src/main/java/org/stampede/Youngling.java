package org.stampede;

import java.util.regex.Pattern;

import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.model.Role;
import org.stampede.model.Status;

public abstract class Youngling {

	protected Stampede stampede;
	
	Youngling(Stampede stampede) {
		this.stampede = stampede;
	}
	
	/**
	 * Register configurations and their corresponding roles or a regex referring to their roles
	 * @param path to the root of the configurations or as close to the root as you're comfortable
	 * @param rolesStrings roles to indicate which leaf folders shall be treated as roles for this instance
	 */
	public final void registerConfig(ConfigMediator configDetails, String... rolesStrings) {
		Pattern[] rolesRegex = new Pattern[rolesStrings.length];
		for(int i = 0 ; i < rolesStrings.length ; i++) {
			rolesRegex[i] = Pattern.compile(rolesStrings[i]);
		}
		registerConfig(configDetails, rolesRegex); 
	}
	
	/**
	 * Register configurations and their corresponding roles or a regex referring to their roles
	 * @param configDetails that describe the location and type of the configs you're looking for
	 * @param rolesRegex a regex pattern to indicate which leaf folders shall be treated as roles for this instance
	 */
	public abstract void registerConfig(ConfigMediator configDetails, Pattern... rolesRegex);

	public abstract Config getConfig();

	public abstract Status getState(String role);

	public abstract Role getRole(String string);

	public abstract Role[] getRoles();
	
}
