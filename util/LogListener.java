/*******************************************************************
 *
 *  $Author: jdomain $
 *  $Date: 2003/12/10 01:12:40 $
 *  $Revision: 1.2 $
 *
 *******************************************************************/

package util;

public interface LogListener {

	//////////////////////////////////////////// Method Declarations

	public void debug(String message);
	public void info(String message);
	public void warning(String message);
	public void warning(String message, Object o);
	public void error(String message);
	public void exception(Throwable e);
	public void close();

}
