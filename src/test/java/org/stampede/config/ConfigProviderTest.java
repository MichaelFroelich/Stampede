package org.stampede.config;

import org.junit.Test;
import org.stampede.Stampede;

public class ConfigProviderTest {

	@Test
	public void start() {
		ConfigProvider configProvider = new ConfigProvider(JsonDeserialiser.class, LocalLocation.class);
		configProvider.get("common").get("fewf").get("rewrqwe").getResult();
	}
}
