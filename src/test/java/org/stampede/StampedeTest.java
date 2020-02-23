package org.stampede;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.config.Deserializer;
import org.stampede.config.Location;
import org.stampede.model.Role;
import org.stampede.model.Status;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;
public class StampedeTest {

	/**
	 * This test is left here to demonstrate how to embed a zookeeper instance,
	 * which will be useful later to prevent zookeeper instances being a potential
	 * point of failure
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void embeddedZookeeper() throws Exception {

		File resourcesDirectory = new File("src/test/resources");
		Properties startupProperties = new Properties();
		startupProperties.setProperty("tickTime", "2000");

		startupProperties.setProperty("clientPort", "2181");
		startupProperties.setProperty("dataDir", "/tmp/zookeeper");
		startupProperties.setProperty("server.1", "server.1=localhost:2888:3888");
		// startupProperties.setProperty("dynamicConfigFile", "/tmp/zookeeper/dynamo");

		/**
		 * Necessary configs tickTime=2000 initLimit=10 syncLimit=5
		 * dataDir=/tmp/zookeeper clientPort=2181
		 * 
		 * server.1=localhost:2888:3888 server.2=localhost:2889:3889
		 * server.3=localhost:2890:3890
		 */
//127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183,127.0.0.1:2184

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
		while (true)
			Thread.sleep(Long.MAX_VALUE);
	}

	@Test
	public void start() throws Exception {

		// Initialize all stampede related stuff
		Stampede stampede = new Stampede("src/test/resources/common/test/stampede.properties");

		// Create a stampede user instance that managers configs and performs role
		// negotiation/election
		Youngling puppy = stampede.getBarn().adoptPup();

		// Add our config folder and a pattern to generate roles from
		puppy.registerConfig(new ConfigMediator("src/test/resources/common/"), "test/v1/*");

		// Test that we can get the root of the
		Config c1 = puppy.getConfig("common").getRoot();
		Role r1 = puppy.getRole("publisher");
		Config c2 = r1.getConfig().getRoot();

		// Test that the config corresponding to the role is equal to the next level of
		// the root
		assertEquals(c1, c2);

		// Get the state of this role
		Status s1 = puppy.getState("publisher");

		// Test that the states are equal
		assertEquals(r1.getState(), s1);

		// For demonstration purposes, also grab all the roles
		Role[] roles = puppy.getRoles();

		// We should have only two, a publisher and a subscriber
		assertEquals(2, roles.length);

		// Finally, close stampede. Stampede should autoclose and free all resources,
		// but it's best to be safe
		stampede.close();
	}

}
