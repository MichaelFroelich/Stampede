package org.stampede;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.spi.SLF4JServiceProvider;

public class Util {
	
	private static final String TESTNETTY = "io.netty.channel.ChannelInboundHandlerAdapter";
	

	public static boolean checkClass(String clazz) {
		try {
			Class cls = Class.forName(clazz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static String safeGetSystemProperty(String key) {
		return safeGetSystemProperty(key, null);
	}

	public static String safeGetSystemProperty(String key, String defaultValue) {
		if (key == null)
			throw new IllegalArgumentException("null input");

		String result = defaultValue;
		try {
			result = System.getProperty(key, defaultValue);
		} catch (java.lang.SecurityException sm) {
			; // ignore
		}
		return result;
	}

	public static boolean safeGetBooleanSystemProperty(String key) {
		String value = safeGetSystemProperty(key, "false");
		if (value == null)
			return false;
		else
			return value.equalsIgnoreCase("true");
	}
	
    private static List<SLF4JServiceProvider> findServiceProviders() {
        ServiceLoader<SLF4JServiceProvider> serviceLoader = ServiceLoader.load(SLF4JServiceProvider.class);
        List<SLF4JServiceProvider> providerList = new ArrayList<SLF4JServiceProvider>();
        for (SLF4JServiceProvider provider : serviceLoader) {
            providerList.add(provider);
        }
        return providerList;
    }
}
