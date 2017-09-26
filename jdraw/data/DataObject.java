package jdraw.data;

import jdraw.Main;
import jdraw.data.event.ChangeEvent;
import jdraw.data.event.EventConstants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import util.Assert;
import util.Log;
import util.Util;

/*
 * DataObject.java - created on 26.10.2003
 * 
 * @author Michaela Behling
 */

public abstract class DataObject
	implements ChangingData, EventConstants, Serializable {

	private static long serialVersionUID = 0L;

	private static int surpressGUIEvents = 0;
	private static ColourSettings backup;

	transient private ArrayList listeners = null;

	private ArrayList getListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		return listeners;
	}

	public final void addDataChangeListener(DataChangeListener listener) {
		if (!getListeners().contains(listener)) {
			getListeners().add(listener);
		}
	}

	public final boolean removeDataChangeListener(DataChangeListener listener) {
		return getListeners().remove(listener);
	}

	public void notifyDataListeners(final ChangeEvent event) {
		if (getListeners().size() == 0) {
			return;
		}
		final ArrayList guiListener = new ArrayList();
		final ArrayList dataListener = new ArrayList();
		Iterator iterator = getListeners().iterator();
		Object o;
		while (iterator.hasNext()) {
			o = iterator.next();
			if (o instanceof DataObject) {
				dataListener.add(o);
			}
			else {
				guiListener.add(o);
			}
		}
		// notify dataobjects synchroniously
		if (dataListener.size() > 0) {
			Iterator dl = dataListener.iterator();
			while (dl.hasNext()) {
				((DataChangeListener) dl.next()).dataChanged(event);
			}
		}
		// notify guiobjects asynchroniously
		if ((!isSurpressingGUIEvents()) && (guiListener.size() > 0)) {
			Runnable runner = new Update(guiListener, event);
			SwingUtilities.invokeLater(runner);
			if (!SwingUtilities.isEventDispatchThread()) {
				Util.delay(200);
			}
		}
	}

	public static boolean isSurpressingGUIEvents() {
		return surpressGUIEvents > 0;
	}

	public static void surpressGUIEvents(Picture pic) {
		if (Main.isVisible(pic)) {
			if (surpressGUIEvents == 0) {
				backup = new ColourSettings(pic);
				if (Log.DEBUG) {
					Log.debug("colour settings saved.");
					Log.debug(backup.toString());
				}
			}
			surpressGUIEvents++;
		}
	}

	public static void allowGUIEvents(Picture pic) {
		if (Main.isVisible(pic)) {
			if (surpressGUIEvents == 1) {
				backup.restore();
			}
			surpressGUIEvents--;
			Assert.isTrue(
				surpressGUIEvents >= 0,
				"data: internal error. supressGUIEvents = " + surpressGUIEvents);
			if (!isSurpressingGUIEvents()) {
				Main.setPicture(backup.picture);
			}
		}
	}

	public void initPicture() {
	}

	private class Update implements Runnable {
		private final ArrayList list;
		private final ChangeEvent event;

		public Update(ArrayList listenerList, ChangeEvent e) {
			list = new ArrayList(listenerList);
			event = e;
		}

		public void run() {
			try {
				DataChangeListener l;
				if (Log.DEBUG)
					Log.debug("-- sending run " + event.toString());
				final int max = list.size();
				for (int i = 0; i < max; i++) {
					l = (DataChangeListener) list.get(i);
					l.dataChanged(event);
				}
			}
			catch (Throwable ex) {
				Log.exception(ex);
			}
		}
	}
}
