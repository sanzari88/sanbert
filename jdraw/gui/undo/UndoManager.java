package jdraw.gui.undo;

import jdraw.action.DrawAction;
import jdraw.action.RedoAction;
import jdraw.action.UndoAction;

import java.util.ArrayList;

import util.Assert;

/*
 * UndoManager.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public final class UndoManager {

	private static final int MAX_UNDOS = 20;
	public static final UndoManager INSTANCE = new UndoManager();

	private final ArrayList buffer = new ArrayList();
	private int pos;

	private UndoManager() {
		reset();
	}

	public void addUndoable(Undoable u) {
		Assert.isTrue(
			u.isValid(),
			"undo: invalid undoable " + u.getClass().getName());
		buffer.add(u);
		if ( buffer.size() > MAX_UNDOS ) {
			buffer.remove(0);		
		}
		pos = buffer.size();
		updateActions();
	}

	private void updateActions() {
		DrawAction.getAction(UndoAction.class).setEnabled(canUndo());
		DrawAction.getAction(RedoAction.class).setEnabled(canRedo());
	}

	public void reset() {
		buffer.clear();
		pos = 0;
		updateActions();
	}

	public void undo() {
		pos--;
		Undoable u = get(pos);
		u.undo();
		updateActions();
	}

	public void redo() {
		Undoable u = get(pos);
		u.redo();
		pos++;
		updateActions();
	}

	private Undoable get(int index) {
		return (Undoable) buffer.get(index);
	}

	public boolean canUndo() {
		return pos != 0;
	}

	public boolean canRedo() {
		return (!buffer.isEmpty()) && (pos < buffer.size());
	}

}
