package jdraw.data.event;

import jdraw.data.DataObject;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public class ChangeEvent implements EventConstants {

	public final int changeType;
	public final DataObject source;
	public final Object firstValue;
	public final Object secondValue;
	public final Object thirdValue;

	public ChangeEvent(DataObject aSource, int aChangeType) {
		this(aSource, aChangeType, null, null);
	}

	public ChangeEvent(DataObject aSource, int aChangeType, Object value) {
		this(aSource, aChangeType, value, null);
	}

	public ChangeEvent(DataObject aSource, int aChangeType, int value) {
		this(aSource, aChangeType, new Integer(value), null);
	}

	public ChangeEvent(
		DataObject aSource,
		int aChangeType,
		int oldValue,
		int newValue) {
		this(aSource, aChangeType, new Integer(oldValue), new Integer(newValue));
	}

	public ChangeEvent(
		DataObject aSource,
		int aChangeType,
		int x,
		int y,
		int col) {
		this(
			aSource,
			aChangeType,
			new Integer(x),
			new Integer(y),
			new Integer(col));
	}

	public ChangeEvent(
		DataObject aSource,
		int aChangeType,
		Object oldValue,
		Object newValue) {
		this(aSource, aChangeType, oldValue, newValue, null);
	}

	public ChangeEvent(
		DataObject aSource,
		int aChangeType,
		Object xValue,
		Object yValue,
		Object colValue) {
		source = aSource;
		changeType = aChangeType;
		firstValue = xValue;
		secondValue = yValue;
		thirdValue = colValue;
	}

	public int getIntValue() {
		return getOldInt();
	}

	public Object getOldValue() {
		return firstValue;
	}

	public Object getNewValue() {
		return secondValue;
	}

	private int toInt(Object o) {
		if (o == null) {
			return -1;
		}
		return ((Integer) o).intValue();
	}

	public int getX() {
		return toInt(firstValue);
	}

	public int getY() {
		return toInt(secondValue);
	}

	public int getColour() {
		return toInt(thirdValue);
	}

	public int getOldInt() {
		return toInt(firstValue);
	}

	public int getNewInt() {
		return toInt(secondValue);
	}

	public String toString() {
		String n = getClass().getName();
		return n.substring(n.lastIndexOf('.') + 1)
			+ "\n   type="
			+ EventConstants.EVENT_NAMES[changeType].toUpperCase()
			+ "\n   source="
			+ source.toString()
			+ "\n   value1="
			+ firstValue
			+ "\n   value2="
			+ secondValue
			+ "\n   value3="
			+ thirdValue;
	}
}
