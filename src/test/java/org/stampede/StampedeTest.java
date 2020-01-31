package org.stampede;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.junit.Test;
import org.stampede.config.Config;
import org.stampede.model.Role;
import org.stampede.model.Status;

public class StampedeTest {
	
	@Test
	public void embeddedZookeeper() throws Exception {

		File resourcesDirectory = new File("src/test/resources");
		Properties startupProperties = new Properties();

		/**
		 * Necessary configs
		tickTime=2000
		initLimit=10
		syncLimit=5
		dataDir=/tmp/zookeeper
		clientPort=2181
		
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890
		 */


		QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
		try {
			quorumConfiguration.parseProperties(startupProperties);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		ZooKeeperServerMain zooKeeperServer = new ZooKeeperServerMain();
		final ServerConfig configuration = new ServerConfig();
		configuration.readFrom(quorumConfiguration);

		new Thread() {
			public void run() {
				try {
					zooKeeperServer.runFromConfig(configuration);
					
				} catch (Exception e) {
				}
			}
		}.start();
	}
	
	@Test
	public void start() throws Exception {
		
		// Initialize all stampede related stuff
		Stampede stampede = new Stampede("src/test/resources/stampede.properties");

		// Create a stampede user instance that managers configs and performs role negotiation/election
		Youngling puppy = stampede.getBarn().adoptPup();
		
		// Add our config folder and a pattern to generate roles from
		puppy.registerConfig("src/test/resources/common/", Pattern.compile("test/v1/*"));
		
		// Test that we can get the root of the 
		Config c1 = puppy.getConfig().get("v1");
		Role r1 = puppy.getRole("publisher");
		Config c2 = r1.getConfig().getParent();
		
		// Test that the config corresponding to the role is equal to the next level of the root
		assertEquals(c1, c2);
		
		// Get the state of this role
		Status s1 = puppy.getState("publisher");
		
		// Test that the states are equal
		assertEquals(r1.getState(), s1);
		
		// For demonstration purposes, also grab all the roles
		Role[] roles = puppy.getRoles();
		
		// We should have only two, a publisher and a subscriber
		assertEquals(2, roles.length);
		
		// Finally, close stampede. Stampede should autoclose and free all resources, but it's best to be safe
		stampede.close();
	}
}
