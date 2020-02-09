package org.stampede.config.deserializer;

import java.io.Reader;
import org.stampede.config.Config;

/**
 * Used for loading .property files
 * 
 * @author Michael
 *
 */
public class ErroredDeserializer implements IConfigDeserializer {

	@Override
	public Config load(Reader stream, Config root) {
		throw new RuntimeException("Serializer failed to initialise");
	}

}
