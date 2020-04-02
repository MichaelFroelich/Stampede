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
        // ObjectOutputStream is used to convert a Java object into OutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        if(stu instanceof Serializable) {
        	oos.writeObject(stu);
        } else {
        	oos.writeChars(String.valueOf(stu));
        }
        stream = baos.toByteArray();
        baos.close();
        oos.close();
        return stream;
	}
	
	public static String getExtension(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return "";
		return str.substring(pos + 1, str.length());
	}

	public static String stripExtension(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;
		return str.substring(0, pos);
	}
	
	public static String getFinalLabel(String fullyQualifiedName) {
		return getExtension(fullyQualifiedName);
	}

}
