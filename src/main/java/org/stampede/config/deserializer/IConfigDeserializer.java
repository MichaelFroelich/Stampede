package org.stampede.config.deserializer;

import java.io.Reader;

import org.stampede.config.Config;

public interface IConfigDeserializer {

	public Config load(Reader path);
}
