package org.stampede.config.location;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.stampede.config.ConfigMediator;

import static java.nio.file.StandardWatchEventKinds.*;

public class LocalLocation implements IConfigLocation {

	private WatchService watchService = null;

	@Override
	public List<Path> register(ConfigMediator config) {
		List<Path> paths = null;
		try (Stream<Path> walk = Files.walk(config.getLocationPath())) {
			if (watchService == null)
				watchService = FileSystems.getDefault().newWatchService();

			paths = walk.filter(Files::isReadable).collect(Collectors.toList());
			for (Path p : paths) {
				p.getParent().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paths;
	}

	/**
	 * 
	 */
	@Override
	public HashMap<Path, FileEvent> poll() {
		WatchKey key;
		HashMap<Path, FileEvent> toreturn = new HashMap<Path, FileEvent>();
		while ((key = watchService.poll()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				Path fullPath = ((Path) key.watchable()).resolve((Path) event.context());
				if (fullPath.toFile().isFile() || kind == ENTRY_CREATE || kind == ENTRY_DELETE) {
					toreturn.put(fullPath, FileEvent.valueOf(kind));
					break;
				}
			}

			// Reset the key -- this step is critical if you want to
			// receive further watch events. If the key is no longer valid,
			// the directory is inaccessible so exit the loop.
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
		return toreturn;
	}
}
