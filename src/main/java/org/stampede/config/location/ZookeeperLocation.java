package org.stampede.config.location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.stampede.Stampede;
import org.stampede.StampedeConfig;
import org.stampede.Util;
import org.stampede.config.ConfigMediator;

public class ZookeeperLocation implements IConfigLocation, Watcher {

	String root;
	Path tempFolder = Paths.get(StampedeConfig.getTemporaryDirectory());
	HashMap<Path, FileEvent> events = new HashMap<Path, FileEvent>();
	HashSet<String> paths = new HashSet<String>();
	CuratorFramework curator = Stampede.getInstance().getCurator();
	
	@Override
	public List<Path> register(ConfigMediator config) {
		ArrayList<Path> toreturn = new ArrayList<Path>();
		try {
			String path = config.getLocationPath().toString().replace("\\", "/");
			download(path, toreturn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toreturn;
	}
	
	private void download(String node, List<Path> toreturn) throws Exception {
		byte[] data = curator.getData().forPath(node);
		if(data.length == 0) {
			for(String child : curator.getChildren().forPath(node)) {
				download(node + "/" + child, toreturn);
			}
		} else {
			Path p = Paths.get(tempFolder.toString(), node);
			File f = p.toFile();
			f.getParentFile().mkdirs();
			if(Util.getExtension(p.toString()).isEmpty()) {
				f.mkdir();
			} else {
				Files.write(Paths.get(tempFolder.toString(), node).toAbsolutePath(), data, StandardOpenOption.CREATE);
			}
			toreturn.add(p);
		}
	}
	
	@Override
	public HashMap<Path, FileEvent> poll() {
		synchronized (events) {
			HashMap<Path, FileEvent> toreturn = new HashMap<Path, FileEvent>(events);
			events.clear();
			return toreturn;
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (paths.contains(event.getPath())) {
			synchronized (events) {
				switch (event.getType()){
					case NodeCreated:
						events.put(Paths.get(event.getPath()), FileEvent.CREATE);
					case NodeDeleted:
						events.put(Paths.get(event.getPath()), FileEvent.DELETE);
					case NodeDataChanged:
						events.put(Paths.get(event.getPath()), FileEvent.MODIFY);
					case NodeChildrenChanged:
					case DataWatchRemoved:
					case ChildWatchRemoved:
					case None:
						break;
				}
			}
		}
	}

}
