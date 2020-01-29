package org.stampede;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.junit.Test;

public class StampedeTest {
	@Test
	public void start() throws Exception {
		
		File resourcesDirectory = new File("src/test/resources");
		String whoami = "no one";
		Properties startupProperties = new Properties();

		/**
		 * Necessary configs
		tickTime=2000
		initLimit=10
		syncLimit=5
		dataDir=/tmp/zookeeper
		clientPort=2181
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

		Stampede stampede = new Stampede(Paths.get("src/test/resources/stampede.properties"),"test");

		Object puppy = stampede.getBarn().adoptPup();
		
		puppy.registerConfig("src/test/resources/common/","test");
		puppy.getConfig().get("Whatever");
		puppet.getState();
		puppy.registerListener(new RoleListener() {
			
			@Override
			public void onUnassignment(String role) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUnadvertise(String role) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAssignment(String role) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAdvertise(String role) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean hasRole(String role) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		stampede.close();

	}
}
