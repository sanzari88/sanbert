package jdraw.gui;

import jdraw.data.Frame;
import jdraw.gui.undo.DrawFill;
import jdraw.gui.undo.DrawPixel;
import jdraw.gui.undo.UndoManager;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/* 
 *
 * @author michaela
 */

public final class FillTool extends Tool {

	public void clicked(int button, Point p) {
		if (p != null) {
			DrawFill f = new DrawFill(button, p.x, p.y);
			if (f.isValid()) {
				UndoManager.INSTANCE.addUndoable(f);
				f.redo();
			}
		}
	}

}
