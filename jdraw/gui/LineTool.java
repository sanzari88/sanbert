package jdraw.gui;

import jdraw.data.Frame;
import jdraw.gui.undo.DrawLine;
import jdraw.gui.undo.DrawPixel;
import jdraw.gui.undo.UndoManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import util.Log;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class LineTool extends RectangularSelectionTool {

	private int x1;
	private int x2;
	private int y1;
	private int y2;

	protected final void processSelection(int mouseButton) {
		DrawLine dl = new DrawLine(mouseButton, start, current);
		if (dl.isValid()) {
			UndoManager.INSTANCE.addUndoable(dl);
			dl.redo();
		}
	}

	private void calculatePoints() {
		x1 = start.x;
		y1 = start.y;
		x2 = current.x;
		y2 = current.y;

		final int grid = getGrid();
		if (grid > 1) {
			final int halfGrid = grid / 2;
			x1 = (x1 * grid) + halfGrid + 2;
			y1 = (y1 * grid) + halfGrid + 2;
			x2 = (x2 * grid) + halfGrid + 2;
			y2 = (y2 * grid) + halfGrid + 2;
		}
	}

	protected void drawRubberBand() {
		if (current == null) {
			return;
		}
		Graphics2D g = (Graphics2D) Tool.getDrawPanel().getGraphics();
		g.setXORMode(Color.white);
		g.setColor(Color.darkGray);
		calculatePoints();
		g.drawLine(x1, y1, x2, y2);
		g.setPaintMode();
	}

}
