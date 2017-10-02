package jdraw.gui;

import jdraw.gui.undo.DrawRectangle;
import jdraw.gui.undo.UndoManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import util.Log;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public abstract class RectangularSelectionTool extends Tool {

	protected static final int INVALID = 0;
	protected static final int DRAGGED = 1;
	protected static final int PRESSED = 2;

	protected int lastAction = -1;
	protected Point start;
	protected Point current;

	protected Point upperLeft;
	protected int width;
	protected int height;

	protected abstract void processSelection(int mouseButton);

	private boolean ignore = false;

	
	public void pressed(int button, Point p) {
		setSwallowKeys(true);
		if (isDragged()) {
			lastAction = DRAGGED;
			if (!ignore) {
				if (p != null) {
					drawRubberBand(); // l�schen
					current = p;
					drawRubberBand();
				}
			}
		}
		else if (p != null) {
			lastAction = PRESSED;
			start = p;
			current = null;
		}
	}

	private void calculateRectangleBounds() {		
		int minX = Math.min(start.x, current.x);
		int minY = Math.min(start.y, current.y);
		upperLeft = new Point(minX, minY);
		int maxX = Math.max(start.x, current.x);
		int maxY = Math.max(start.y, current.y);
		width = (maxX - minX) + 1;
		height = (maxY - minY) + 1;
	}

	public final void released(int button, Point p) {
		if (lastAction == DRAGGED) {
			drawRubberBand();
			setSwallowKeys(false);
			processSelection(button);
		}
	}

	public static final boolean isValidSelection(Rectangle r) {
		return Tool.getCurrentFrame().getBounds().contains(r);
	}

	public static final boolean isValidSelection(
		int x,
		int y,
		int aWidth,
		int aHeight) {
		return isValidSelection(new Rectangle(x, y, aWidth, aHeight));
	}

	protected final boolean isValidSelection() {
		return isValidSelection(upperLeft.x, upperLeft.y, width, height);
	}

	protected void drawRubberBand() {
		if (current == null) {
			return;
		}
		calculateRectangleBounds();
		if (isValidSelection()) {
			Graphics2D g = (Graphics2D) Tool.getDrawPanel().getGraphics();
			g.setXORMode(Color.white);
			g.setColor(Color.darkGray);
			final int grid = getGrid();
			g.drawRect(
				(upperLeft.x * grid),
				(upperLeft.y * grid),
				width * grid,
				height * grid);
			g.setPaintMode();
		}
	}

	protected void entered(int button, Point p) {
		ignore = false;
	}

	protected void exited(int button, Point p) {
		ignore = true;
	}

}
