package org.stampede.config.location;

import java.nio.file.WatchEvent;

import java.nio.file.StandardWatchEventKinds;

public enum FileEvent {

	CREATE, MODIFY, DELETE, OVERFLOW, ERROR;
	
	public static FileEvent valueOf(WatchEvent.Kind<?> kind) {
		if(kind == StandardWatchEventKinds.ENTRY_CREATE)
			return CREATE;
		else if(kind == StandardWatchEventKinds.ENTRY_MODIFY)
			return MODIFY;
		else if(kind == StandardWatchEventKinds.ENTRY_DELETE)
			return DELETE;
		else if(kind == StandardWatchEventKinds.OVERFLOW)
			return OVERFLOW;
		else return ERROR;
	}
}
