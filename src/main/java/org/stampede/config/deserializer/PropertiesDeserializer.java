package org.stampede.config.deserializer;

import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;
import org.stampede.config.Config;

/**
 * Used for loading .property files
 * 
 * @author Michael
 *
 */
public class PropertiesDeserializer implements IConfigDeserializer {

	@Override
	public Config load(Reader stream, Config root) {
		Properties prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Entry<Object, Object> entry : prop.entrySet()) {
			if (entry.getKey() instanceof String) {
				new Config(entry.getValue(), root, (String) entry.getKey());
			}
		}
		return root;
	}

}
