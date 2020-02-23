package org.stampede.config;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Test;

public class ConfigProviderTest {

	@Test
	public void testProperties() throws Exception {

		ConfigMediator configProvider = new ConfigMediator(Deserializer.Properties, Location.Local,
				"src/test/resources");
		Config testConfig = configProvider.get("common").get("test");
		String clientPort = (String) testConfig.get("stampede").get("configurationnumber").getResult();
		assertEquals( "2181", clientPort);
		String applicationName = (String) testConfig.get("stampede").get("application.version").getResult();
		assertEquals("somename", applicationName);

		//Change the file
		Path path = Paths.get("src/test/resources/common/test/stampede.properties");
		String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		content = content.replaceAll("somename", "notactuallyaversionstill");
		Files.write(path, content.getBytes(StandardCharsets.UTF_8));
		Thread.sleep(1001); //wait just over a second
		applicationName = (String) testConfig.get("stampede").get("application.version").getResult();
		assertEquals("notactuallyaversionstill", applicationName);
		
		//Add a file
		path = Paths.get("src/test/resources/common/test/newconfig.properties");
		content = "config=now for something completely different";
		Files.write(path, content.getBytes(StandardCharsets.UTF_8));
		Thread.sleep(1001); //wait just over a second
		applicationName = (String) testConfig.get("newconfig").get("config").getResult();
		assertEquals("now for something completely different", applicationName);
		
		configProvider.close();
	}
	

    @AfterClass
    public static void clean() throws IOException {
		Path path = Paths.get("src/test/resources/common/test/stampede.properties");
		String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		content = content.replaceAll("notactuallyaversionstill", "somename");
		Files.write(path, content.getBytes(StandardCharsets.UTF_8));
		Files.delete( Paths.get("src/test/resources/common/test/newconfig.properties"));
    }    

}
