package jdraw.gui;

import jdraw.gui.undo.DrawOval;
import jdraw.gui.undo.DrawRectangle;
import jdraw.gui.undo.UndoManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/* 
 *
 * @author michaela
 */

public final class OvalTool extends RectangularSelectionTool {

	private final boolean fillOval;

	public OvalTool() {
		this(false);
	}

	public OvalTool(boolean doFill) {
		fillOval = doFill;
	}

	protected void processSelection(int mouseButton) {
		DrawOval dov = new DrawOval(mouseButton, fillOval, start, current);
		if (dov.isValid()) {
			UndoManager.INSTANCE.addUndoable(dov);
			dov.redo();
		}
	}

	public boolean supportsGradientFill() {
		return fillOval;
	}

	public boolean supportsAntialias() {
		return true;
	}

}
