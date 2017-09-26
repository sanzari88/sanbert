/*******************************************************************
 *
 *  $Author: jdomain $
 *  $Date: 2003/12/10 09:46:12 $
 *  $Revision: 1.3 $
 *
 *******************************************************************/

package util;

public final class Assert {
	//////////////////////////////////////////// Members

	//////////////////////////////////////////// Constructors

	private Assert() {
	}

	//////////////////////////////////////////// Static Methods

	public static void fail(String message) {		
		throw new RuntimeException(message);
	}

	public static void equals(Object a, Object b, String message) {
		if ((a == null) && (b == null))
			return;
		if ((a == null) || (b == null))
			fail(message);

		if (!a.equals(b))
			fail(message);
	}

	public static void isTrue(boolean aFlag, String message) {
		if (!aFlag)
			fail(message);
	}

	public static void isFalse(boolean aFlag, String message) {
		if (aFlag)
			fail(message);
	}

	public static void notNull(Object anObj, String message) {
		if (anObj == null)
			fail(message);
	}

	public static void isNull(Object anObj, String message) {
		if (anObj != null)
			fail(message);
	}

	//////////////////////////////////////////// Instance Methods

	//////////////////////////////////////////// Inner classes

}
