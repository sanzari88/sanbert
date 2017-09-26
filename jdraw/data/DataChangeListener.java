package jdraw.data;

import jdraw.data.event.ChangeEvent;
import jdraw.data.event.EventConstants;

/*
 * DataChangeListener.java - created on 26.10.2003
 * 
 * @author Michaela Behling
 */

public interface DataChangeListener extends EventConstants {

	void dataChanged(ChangeEvent e);
}
