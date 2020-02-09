package org.stampede.config.location;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import org.stampede.config.ConfigMediator;

public class ErroredLocation implements IConfigLocation {

	@Override
	public List<Path> register(ConfigMediator config) {
		throw new RuntimeException("Location failed to initialise");
	}

	/**
	 * 
	 */
	@Override
	public HashMap<Path, FileEvent> poll() {
		throw new RuntimeException("Location failed to initialise");
	}
}
