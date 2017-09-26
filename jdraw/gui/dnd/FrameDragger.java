package jdraw.gui.dnd;

import jdraw.gui.FrameSettingsDialog.FrameEntry;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.*;
import java.util.TooManyListenersException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import util.Assert;
import util.Log;

/*
 * FrameDragSource.java - created on 03.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class FrameDragger
	implements DragSourceListener, DragGestureListener, DropTargetListener {

	private final JList frameList;
	private final DragSource source;
	private FrameEntry entry;
	private int action;
	private int originalPosition;
	private boolean dndStarted = false;

	public FrameDragger(JList aList) {
		frameList = aList;
		source = new DragSource();
		source.createDefaultDragGestureRecognizer(
			aList,
			DnDConstants.ACTION_COPY_OR_MOVE,
			this);
		DropTarget target = new DropTarget();
		target.setComponent(frameList);
		try {
			target.addDropTargetListener(this);
		}
		catch (TooManyListenersException e) {
			Log.exception(e);
		}
	}

	// DragSourceListener
	public void dragDropEnd(DragSourceDropEvent dsde) {
		dndStarted = false;
	}

	public void copyFrame(Point p) {
		DefaultListModel model = (DefaultListModel) frameList.getModel();
		int newPos = getListIndex(p);
		frameList.clearSelection();
		if (newPos == -1) {
			model.addElement(entry.createCopy());
			frameList.setSelectedIndex(model.getSize() - 1);
		}
		else {
			model.insertElementAt(entry.createCopy(), newPos + 1);
			frameList.setSelectedIndex(newPos + 1);
		}
	}

	public void moveFrame(Point p) {
		DefaultListModel model = (DefaultListModel) frameList.getModel();
		int size = model.getSize();
		if (size == 1) { // nothing to move			
			return;
		}
		int newPos = getListIndex(p);

		if (newPos == -1) {
			newPos = size;
		}
		if (newPos != originalPosition) {
			frameList.clearSelection();
			model.removeElement(entry);
			if (newPos == size) {
				newPos--;
			}
			size = model.getSize();

			if (newPos == size) {
				model.addElement(entry.createIdenticalCopy());
				frameList.setSelectedIndex(size);
			}
			else {
				model.insertElementAt(entry.createIdenticalCopy(), newPos);
				frameList.setSelectedIndex(newPos);
			}
		}
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		action = dsde.getUserAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		}
		else if (action == DnDConstants.ACTION_MOVE) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		}
	}

	private final int getListIndex(Point p) {
		final int height = frameList.getFixedCellHeight();
		Assert.isTrue(height != -1, "dnd: frame list must have fixed row height");
		int y = 0;
		final int frameCount = frameList.getModel().getSize();
		for (int i = 0; i < frameCount; i++) {
			if ((p.y >= y) && (p.y < y + height)) {
				return i;
			}
			y = y + height;
		}
		return -1;
	}

	private FrameEntry findFrameEntry(Point p) {
		originalPosition = getListIndex(p);
		return (FrameEntry) frameList.getModel().getElementAt(originalPosition);
	}

	private void start(DragGestureEvent e) {
		if (dndStarted) {
			Log.info("Drag'N Drop already started.");
			return;
		}
		dndStarted = true;
		entry = findFrameEntry(e.getDragOrigin());
		Assert.notNull(entry, "dnd: no frame entry found");

		Cursor cursor = DragSource.DefaultMoveDrop;
		if (e.getDragAction() == DnDConstants.ACTION_COPY) {
			cursor = DragSource.DefaultCopyDrop;
		}

		source.startDrag(e, cursor, entry, this);
	}

	// DragGestureListener
	public void dragGestureRecognized(DragGestureEvent dge) {
		action = dge.getDragAction();
		if ((action == DnDConstants.ACTION_COPY)
			|| (action == DnDConstants.ACTION_MOVE)) {
			start(dge);
		}
		else if (Log.DEBUG) {
			Log.debug("drag gesture ignored");
		}
	}

	public void dragEnter(DropTargetDragEvent dtde) {

	}

	public void dragExit(DropTargetEvent dte) {

	}

	public void dragOver(DropTargetDragEvent dtde) {

	}

	public void drop(DropTargetDropEvent dtde) {
		if (action == DnDConstants.ACTION_COPY) {
			copyFrame(dtde.getLocation());
		}
		else if (action == DnDConstants.ACTION_MOVE) {
			moveFrame(dtde.getLocation());
		}
		else {
			Log.warning("Drag action '" + action + "' not supported.");
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

}
