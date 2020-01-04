package org.stampede.config.location;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

public class LocalLocation implements IConfigLocation {

	private WatchService watchService = null;
	
	@Override
	public void watch(String uri) {
		try {
			if(watchService == null)
				watchService = FileSystems.getDefault().newWatchService();
			
			Path path = Paths.get(uri);
			path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			registerRecursive(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (;;) {

		    // wait for key to be signaled
		    WatchKey key;
		    try {
		        key = watchService.take();
		    } catch (InterruptedException x) {
		        return;
		    }

		    for (WatchEvent<?> event: key.pollEvents()) {
		        WatchEvent.Kind<?> kind = event.kind();

		        // This key is registered only
		        // for ENTRY_CREATE events,
		        // but an OVERFLOW event can
		        // occur regardless if events
		        // are lost or discarded.
		        if (kind == OVERFLOW) {
		            continue;
		        }

		        
		        
		        // The filename is the
		        // context of the event.
		        if(event.context() instanceof Path) {
			        Path filename = (Path)event.context();
			        System.out.format("Emailing file %s%n", filename);
			        //Details left to reader....
		        }
		    }

		    // Reset the key -- this step is critical if you want to
		    // receive further watch events.  If the key is no longer valid,
		    // the directory is inaccessible so exit the loop.
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
		
		
		ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		/*This schedules a runnable task every second*/
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
		  public void run() {
			  //here
		  }
		}, 0, 1, TimeUnit.SECONDS);
	}

	private void registerRecursive(final Path root) throws IOException {
	    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	            dir.register(watchService,  ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	            return FileVisitResult.CONTINUE;
	        }
	    });
	}
	
}
