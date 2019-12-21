package org.stampede;


public class Util {
    
    public static String safeGetSystemProperty(String key) {
        return safeGetSystemProperty(key, null);
    }
    
    public static String safeGetSystemProperty(String key, String defaultValue) {
        if (key == null)
            throw new IllegalArgumentException("null input");

        String result = null;
        try {
            result = System.getProperty(key);
        } catch (java.lang.SecurityException sm) {
            ; // ignore
        }
        return result;
    }

    public static boolean safeGetBooleanSystemProperty(String key) {
        String value = safeGetSystemProperty(key);
        if (value == null)
            return false;
        else
            return value.equalsIgnoreCase("true");
    }
}
