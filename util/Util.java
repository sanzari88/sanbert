package util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

/*
 * Util.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class Util {

	private Util() {
	}

	public static String shortClassName(Class c) {
		String s = c.getName();
		int index = s.lastIndexOf('.');
		if (index == -1) {
			return s;
		}
		return s.substring(index + 1);
	}

	public static String getFileExtension(String fileName) {
		final int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return "";
		}
		return fileName.substring(index);
	}

	public static String getFileExtension(File file) {
		return getFileExtension(file.toString());
	}

	public static Object createInstance(Class aClass, Object[] parameters) {
		try {
			if (parameters == null) {
				parameters = new Object[0];
			}
			final int paramCount = parameters.length;
			Class[] types = new Class[paramCount];
			for (int i = 0; i < paramCount; i++) {
				types[i] = parameters[i].getClass();
			}
			Constructor con = aClass.getConstructor(types);
			if (con == null) {
				return null;
			}
			return con.newInstance(parameters);
		}
		catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	public static boolean delay(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
			return true;
		}
		catch (InterruptedException e) {
			Log.exception(e);
			return false;
		}
	}

	public static boolean isIn(int value, int min, int max) {
		return (value >= min) && (value <= max);
	}

	public static Exception close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			}
			catch (Exception e) {
				return e;
			}
		}
		return null;
	}

	public static Exception close(OutputStream out) {
		if (out != null) {
			try {
				out.flush();
				out.close();
			}
			catch (Exception e) {
				return e;
			}
		}
		return null;
	}

	public static StrippedHTMLString stripHTMLCode(String s) {
		boolean inHTML = false;
		String lastToken = "";
		String token;
		StringBuffer buf = new StringBuffer();
		int htmlStart = -1;
		int index = 0;
		StringTokenizer st = new StringTokenizer(s, "<>\\", true);
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("<")) { // html start?
				if (lastToken.equals("\\")) {
					buf.append(token);
				}
				else {
					inHTML = true;
					htmlStart = index;
				}
			}
			else if (token.equals(">")) { // html end?
				if (lastToken.equals("\\")) {
					buf.append(token);
				}
				else if (inHTML) {
					inHTML = false;
					htmlStart = -1;
				}
				else {
					buf.append(token);
				}
			}
			else { // text in or out of html
				if (!inHTML) {
					buf.append(token);
				}
			}
			index = index + token.length();
		}
		return new StrippedHTMLString(s, buf.toString(), htmlStart);
	}

	public static String asBytes(String s) {
		StringBuffer buf = new StringBuffer();
		final int len = s.length();
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				buf.append(' ');
			}
			buf.append(hexString((int) s.charAt(i)));
		}
		return buf.toString();
	}

	public static int asInt(String s) {
		return asInt(s, 10);
	}

	public static int asInt(String s, int radix) {
		return asInt(s, radix, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static int asInt(String s, int radix, int min, int max) {
		if (isNumber(s, radix, min, max)) {
			return Integer.parseInt(s, radix);
		}
		return 0;
	}

	public static String binaryString(final int b, final int length) {
		StringBuffer buf = new StringBuffer(Integer.toBinaryString(b));
		if (buf.length() < length) {
			while (buf.length() < length) {

				buf.insert(0, '0');
			}
		}
		else {
			while (buf.length() > length) {
				buf.deleteCharAt(0);
			}
		}
		return buf.toString();
	}

	public static String binaryString(final int b) {
		return binaryString(b, 8);

	}

	public static String hexString(final int b, final int length) {
		StringBuffer buf = new StringBuffer(Integer.toHexString(b));
		while (buf.length() < length) {

			buf.insert(0, '0');
		}
		return buf.toString().toUpperCase();
	}

	public static String hexString(final int b) {
		return hexString(b, 2);

	}

	public static String string(final int b, final int length) {
		StringBuffer buf = new StringBuffer(String.valueOf(b));
		while (buf.length() < length) {
			buf.insert(0, ' ');
		}
		return buf.toString();
	}

	public static boolean isNumber(String aNumber) {
		return isNumber(aNumber, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static boolean isMinMax(int value, int min, int max) {
		return (value >= min) && (value <= max);
	}

	public static boolean isNumber(String aNumber, int radix) {
		return isNumber(aNumber, radix, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static boolean isNumber(String aNumber, int min, int max) {
		return isNumber(aNumber, 10, min, max);
	}

	public static boolean isNumber(
		String aNumber,
		int radix,
		int min,
		int max) {
		if (aNumber == null) {
			return false;
		}
		String s = aNumber.trim();
		if (s.length() == 0) {
			return false;
		}
		try {
			int i = Integer.parseInt(aNumber, radix);
			return (i >= min) && (i <= max);
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

	public static class StrippedHTMLString {
		public final String source;
		public final String stripped;
		public final boolean endsInHTML;
		public final String trailingHTML;

		protected StrippedHTMLString(
			String aSource,
			String aStrippedString,
			int lastHTMLStart) {
			source = aSource;
			stripped = aStrippedString;
			endsInHTML = (lastHTMLStart != -1);
			if (endsInHTML) {
				trailingHTML = source.substring(lastHTMLStart);
			}
			else {
				trailingHTML = null;
			}
		}

		public String toString() {
			return "\nsource: '"
				+ source
				+ "'"
				+ "\nstripped: '"
				+ stripped
				+ "'\ntrailingHTML: "
				+ (endsInHTML ? "'" + trailingHTML + "'" : "<empty>");
		}
	}

}
