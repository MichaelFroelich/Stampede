package org.stampede.config;

import org.junit.Test;

public class ConfigProviderTest {

	@Test
	public void start() throws InstantiationException, IllegalAccessException {
		
		ConfigFacade configProvider = new ConfigFacade(Deserializer.Properties, Location.Local, "");

		configProvider.get("common").get("fewf").get("rewrqwe").getResult();
	}
}
