package org.stampede.youngling;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.stampede.Stampede;
import org.stampede.Util;
import org.stampede.Youngling;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.model.Role;
import org.stampede.model.Status;

public class Pup extends Youngling implements IRoar, IDominant {
	
	public Pup(Stampede stampede) {
		super(stampede);
	}

	@Override
	public void registerConfig(ConfigMediator configDetails, String... rolesMatcher) {
		super.registerConfig(configDetails, rolesMatcher);
		try {
			roar(stampede, configDetails);
		}
		catch (Exception e) {
			logger.error("Failed to roar because " + e.getMessage());
		}
	}

	@Override
	protected void advertise() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unadvertise() {
		// TODO Auto-generated method stub
		
	}
}
