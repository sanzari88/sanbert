/*******************************************************************
 *
 *  $Author: Michaela $
 *  $Date: 2003/12/06 06:03:28 $
 *  $Revision: 1.14 $
 *
 *******************************************************************/

package util;

import java.util.ArrayList;

public final class Log {
	//////////////////////////////////////////// Members

	private static ArrayList listeners = new ArrayList();
	private static boolean on = true;
	public static final boolean DEBUG = true;

	//////////////////////////////////////////// Constructors

	private Log() {
	}

	//////////////////////////////////////////// Static Methods

	public static void turnOff() {
		on = false;
	}
	
	public static void turnOn() {
		on = true;
	}


	public static void addLogListener(LogListener aListener) {
		listeners.add(aListener);
	}

	public static void removeListener(LogListener aListener) {
		listeners.remove(aListener);
	}
	
	private static int getListenerCount() {
		if ( !on ) {
			return 0;
		}
		return listeners.size();
	}

	public static void close() {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.close();
		}
	}

	// Log messages

	public static void debug(String message) {
		if (!DEBUG)
			return;

		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.debug(message);
		}
	}

	public static void info(String message) {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.info(message);
		}
	}

	public static void warning(String message) {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.warning(message);
		}
	}

	public static void warning(String message, Object o) {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.warning(message, o);
		}
	}

	public static void error(String message) {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.error(message);
		}
	}

	public static void exception(Throwable e) {
		final int max = getListenerCount();
		LogListener l;
		for (int i = 0; i < max; i++) {
			l = (LogListener) listeners.get(i);
			l.exception(e);
		}
		if ( DEBUG ) {
			System.exit(1);
		}
	}

	//////////////////////////////////////////// Instance Methods

	//////////////////////////////////////////// Inner classes

}
