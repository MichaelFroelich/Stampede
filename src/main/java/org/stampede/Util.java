package org.stampede;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.stampede.model.Role;

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

	public static String scanZookeeper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static boolean isRegex(String input) {
		boolean isRegex;
		try {
		  Pattern.compile(input);
		  isRegex = true;
		} catch (PatternSyntaxException e) {
		  isRegex = false;
		}
		return isRegex;
	}

	public static byte[] binarySerialize(Object stu) throws IOException {
        // Reference for stream of bytes
        byte[] stream = null;
        Object serializable;
        if(stu instanceof Serializable) {
        	serializable = stu;
        } else {
        	serializable = String.valueOf(stu);
        }
        // ObjectOutputStream is used to convert a Java object into OutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serializable);
        stream = baos.toByteArray();
        baos.close();
        oos.close();
        return stream;
	}
}
