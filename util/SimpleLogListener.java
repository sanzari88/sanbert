/*******************************************************************
 *
 *  $Author: jdomain $
 *  $Date: 2003/12/10 01:12:40 $
 *  $Revision: 1.2 $
 *
 *******************************************************************/

package util;

import java.io.*;

public final class SimpleLogListener implements LogListener {
	private PrintStream out;

	public SimpleLogListener(PrintStream aStream) {
		out = aStream;
	}

	public SimpleLogListener(String fileName) {
		try {
			FileOutputStream fOut = new FileOutputStream(fileName);
			out = new PrintStream(fOut);
		}
		catch (Exception e) {
			Assert.fail("Logging error! " + e.getMessage());
		}
	}

	//////////////////////////////////////////// Methods

	private void print(String message) {
		out.println(message);
	}

	public void debug(String message) {
		print(message);
	}

	public void info(String message) {
		print(message);
	}

	public void warning(String message) {
		print("[warning] " + message);
	}

	public void warning(String message, Object o) {
		print("[warning] " + message);
		print("[" + o.toString() + "]");
	}

	public void error(String message) {
		print("[error] " + message);
	}

	public void exception(Throwable e) {
		print("[exception] " + e.getMessage());
		e.printStackTrace(out);
	}

	public void close() {
		out.flush();
		out.close();
	}

}
