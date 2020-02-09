package org.stampede.config.location;

import java.nio.file.Path;
import java.util.HashMap;

public abstract class GitLocation implements IConfigLocation {

	public void register(String uri) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public HashMap<Path, FileEvent> poll() {
		// TODO Auto-generated method stub
		return null;
	}

}
