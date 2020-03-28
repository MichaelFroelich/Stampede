package org.stampede;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.curator.test.TestingServer;
import org.junit.Test;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.config.Deserializer;
import org.stampede.config.Location;
import org.stampede.model.Role;

public class YounglingTest {
	
	@Test
	public void testConfigPublishPup() throws Exception {
		TestingServer server = new TestingServer();
		System.setProperty("zookeeper.connectionString", server.getConnectString());
		Stampede stampede = Stampede.getInstance(); //do not load init config
		
		Youngling puppy = stampede.getBarn().adoptPup();
		ConfigMediator configProvider = new ConfigMediator(Deserializer.Properties, Location.Local,
				"src/test/resources");
		puppy.registerConfig(configProvider, "**/test/v1/*");
		String expected = (String)(puppy.getConfigs()[0].get("common").get("init").get("stampede").get("name").getResult());
		
		
		Youngling calf = stampede.getBarn().adoptCalf();
		calf.registerConfig(new ConfigMediator(Deserializer.Properties, Location.ZooKeeper, "/common/"),  "**/test/v1/*");
		String actual = (String)(calf.getConfigs()[0].get("common").get("init").get("stampede").get("name").getResult());
		
		assertTrue(expected.equals(actual));
		
		for(Role r : puppy.getRoles()) {
			stampede.getCuratorClient().delete().forPath(r.getConfig().getPath());
		}
		
		server.close();
		stampede.close();
	}
}
