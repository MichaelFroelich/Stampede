package org.stampede;

import org.stampede.config.Config;

/**
 * Lots of boiler plate stuff that checks properties since java properties may
 * be used instead of specifying an initial config file
 * 
 * @author Michael
 *
 */
public class StampedeConfig {
	private static final String DATADIR = "dataDir";
	private static final String DATADIR_DEFAULT = "/tmp";
	private static final String STAMPEDE_SOCKET = "stampede.socket";
	private static final String STAMPEDE_CLIENTPORT = "stampede.clientPort";
	private static final String STAMPEDE_CLIENTPORT_DEFAULT = "1024";
	private static final String STAMPEDE_DATADIR = "stampede.dataDir";
	private static final String STAMPEDE_DATADIR_DEFAULT = DATADIR;
	private static final String STAMPEDE_NAME = "stampede.name";
	private static final String STAMPEDE_SHOULD_SERIALIZE_WHOLE_TREE = "stampede.serializeWholeTree";
	private static final String ZOOKEEPER_CLIENTPORT = "zookeeper.clientPort";
	private static final String ZOOKEEPER_CLIENTPORT_DEFAULT = "2181";
	private static final String ZOOKEEPER_CONNECTIONSTRING = "zookeeper.connectionString";
	private static final String ZOOKEEPER_TICKTIME = "zookeeper.tickTime";
	private static final String ZOOKEEPER_TICKTIME_DEFAULT = "2000";
	private static final String ZOOKEEPER_INITLIMIT = "zookeeper.initLimit";
	private static final String ZOOKEEPER_INITLIMIT_DEFAULT = "10";
	private static final String ZOOKEEPER_MAXRETRIES = "zookeeper.maxRetries";
	private static final String ZOOKEEPER_MAXRETRIES_DEFAULT = "3";
	private static final String ZOOKEEPER_MAXRETRYTIME = "zookeeper.maxRetryTime";
	private static final String ZOOKEEPER_MAXRETRYTIME_DEFAULT = "3000";
	private static final String ZOOKEEPER_SYNCLIMIT = "zookeeper.syncLimit";
	private static final String ZOOKEEPER_SYNCLIMIT_DEFAULT = "5";
	private static final String ZOOKEEPER_MAXSESSIONTIMEOUT = "zookeeper.maxSessionTimeout";
	private static final String ZOOKEEPER_MAXSESSIONTIMEOUT_DEFAULT = "60000";
	private static final String ZOOKEEPER_DATADIR = "zookeeper.dataDir";
	private static final String ZOOKEEPER_DATADIR_DEFAULT = DATADIR;

	static Config _internalConfig;

	public static void init(Config internalConfig) {
		_internalConfig = internalConfig;
	}

	public static String getConnectionString() {
		return get(ZOOKEEPER_CONNECTIONSTRING);
	}

	public static int getZookeeperTickTime() {
		return Integer.valueOf(get(ZOOKEEPER_TICKTIME, ZOOKEEPER_TICKTIME_DEFAULT));
	}

	public static int getZookeeperMaxSessionTimeout() {
		return Integer.valueOf(get(ZOOKEEPER_MAXSESSIONTIMEOUT, ZOOKEEPER_MAXSESSIONTIMEOUT_DEFAULT));
	}

	public static int getZookeeperMaxRetryTime() {
		return Integer.valueOf(get(ZOOKEEPER_MAXRETRYTIME, ZOOKEEPER_MAXRETRYTIME_DEFAULT));
	}

	public static int getZookeeperMaxRetryCount() {
		return Integer.valueOf(get(ZOOKEEPER_MAXRETRIES, ZOOKEEPER_MAXRETRIES_DEFAULT));
	}

	public static String getSocketImplementation() {
		return get(STAMPEDE_SOCKET);
	}
	
	public static boolean shouldSerializeWholeTree() {
		return Boolean.valueOf(get(STAMPEDE_SHOULD_SERIALIZE_WHOLE_TREE, false));
	}

	private static String get(String key) {
		return get(key, null);
	}

	private static String get(String key, Object defaulte) {
		Object toreturn = defaulte;
		try {
			if (_internalConfig != null) {
				toreturn = _internalConfig.get(key).getResult().toString();
			}
		} catch (Exception dontcare) {

		}
		try {
			if (toreturn == null) {
				toreturn = System.getProperty(key, String.valueOf(defaulte));
			}
		} catch (Exception dontcare) {

		}
		return toreturn.toString();
	}
}
