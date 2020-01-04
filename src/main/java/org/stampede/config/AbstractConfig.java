package org.stampede.config;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfig implements Closeable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String LinuxLocal = "\\";

	public static final String WindowsLocal = "C:\\";

	ArrayList<Path> paths;

	ConcurrentHashMap<String, String> keyValuePairs;

	public AbstractConfig(String rootFolder) {
		keyValuePairs = new ConcurrentHashMap<String, String>(100);
		paths = new ArrayList<Path>(10);
		try {
			switch (rootFolder.charAt(0)){
				case '\\':
					break;
				case 'C':
					break;
				default:
					break;
			}
		} catch (Exception e) {

		}

	}

	public Object deserialise(Path path) throws IOException {
		if (path.toString().toLowerCase().endsWith(".properties")) {
			Properties prop = new Properties();
			Reader targetReader = Files.newBufferedReader(path, Charset.defaultCharset());
			prop.load(targetReader);
			System.setProperties(prop);
			targetReader.close();
		}

		return null;
	}

	protected InputStream openFolder(String pathDirectory) throws InterruptedException, IOException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(pathDirectory);

		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				Path p = (Path) event.context();
				deserialise(p);
				logger.trace("File event kind:" + event.kind() + ". File affected: " + event.context() + ".");

			}
			key.reset();
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
}
