package jdraw.data;


/*
 * ChangingData.java - created on 26.10.2003
 * 
 * @author Michaela Behling
 */

public interface ChangingData {
	void addDataChangeListener( DataChangeListener aListener );
	boolean removeDataChangeListener( DataChangeListener aListener );
}
