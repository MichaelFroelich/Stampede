package org.stampede.config.deserializer;

import java.io.Reader;

import org.stampede.config.Config;

public interface IConfigDeserializer {

	/**
	 * @param stream to a file containing the config
	 * @param root config file
	 * @return
	 */
	public Config load(Reader stream, Config root);
}
