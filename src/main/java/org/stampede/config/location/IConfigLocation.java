package org.stampede.config.location;

import java.nio.file.Path;
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
	 * @param path
	 * @return local copies of files that have successfully registered
	 */
	public List<Path> register(ConfigMediator path);

	/**
	 * 
	 * @return local copies of files that have changed with a value of how they
	 *         changed
	 */
	public HashMap<Path, FileEvent> poll();
}
