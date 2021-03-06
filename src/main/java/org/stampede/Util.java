package org.stampede;

public class Util {
	
	public static boolean checkClass(String clazz) {
		try {
			Class.forName(clazz);
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
	
	public static String getCallingClassName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		int i = 1;
		StackTraceElement e = stacktrace[i];
		String className = getFinalLabel(e.getClassName());
		while(className.equals(Stampede.class.getSimpleName()) || className.equals(Util.class.getSimpleName())) {
			e = stacktrace[i++];
			className = getFinalLabel(e.getClassName());
		}
		return className;
	}
	
	public static String getFinalLabel(String fullyQualifiedName) {
		return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1).trim();
	}
}
