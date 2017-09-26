package jdraw.gui;

import jdraw.gui.undo.DrawRectangle;
import jdraw.gui.undo.UndoManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class RectangleTool extends RectangularSelectionTool {
	
	
	private final boolean filled;

	public RectangleTool() {
		this(false);
	}

	public RectangleTool(boolean isFilled ) {
		filled = isFilled; 
	}
	
	
	
	public boolean supportsGradientFill() {
		return filled;
	}

	protected void processSelection(int mouseButton) {
		DrawRectangle dr =
			new DrawRectangle(mouseButton, filled, start, current);
		if (dr.isValid()) {
			UndoManager.INSTANCE.addUndoable(dr);
			dr.redo();
		}
	}

}
