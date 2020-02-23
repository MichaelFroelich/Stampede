package org.stampede;

import org.apache.curator.test.TestingServer;
import org.junit.Test;
import org.stampede.config.ConfigMediator;
import org.stampede.config.Deserializer;
import org.stampede.config.Location;
import org.stampede.model.Role;

public class YounglingTest {
	
	@Test
	public void testConfigPublishPup() throws Exception {
		TestingServer server = new TestingServer();
		System.setProperty("zookeeper.connectionString", server.getConnectString());
		Stampede stampede = new Stampede(); //do not load init config
		
		Youngling puppy = stampede.getBarn().adoptPup();
		ConfigMediator configProvider = new ConfigMediator(Deserializer.Properties, Location.Local,
				"src/test/resources");
		puppy.registerConfig(configProvider, "**/test/v1/*");
		
		for(Role r : puppy.getRoles()) {
			stampede.getCuratorClient().delete().forPath(r.getConfig().getPath());
		}
		
		Thread.sleep(Long.MAX_VALUE);
		
		server.close();
		stampede.close();
	}
}
