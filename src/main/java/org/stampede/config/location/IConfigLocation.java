package org.stampede.config.location;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.stampede.Util;
import org.stampede.config.ConfigMediator;

public interface IConfigLocation {

	default String getTemporaryDirectory() {
		return Util.safeGetSystemProperty("dataDir", "/tmp/");
	}

	/**
	 * Should register all files recursively within a path
	 * 
	 * @param config
	 * @return local copies of files that have successfully registered
	 */
	default public List<Path> register(ConfigMediator config) {
		ArrayList<Path> toreturn = new ArrayList<Path>(1);
		toreturn.add(Paths.get(getTemporaryDirectory()));
		return toreturn;
	}
	

	/**
	 * 
	 * @return local copies of files that have changed with a value of how they
	 *         changed
	 */
	public HashMap<Path, FileEvent> poll();
}
