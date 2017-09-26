package jdraw.gui;

import jdraw.action.CropAction;
import jdraw.action.DrawAction;
import jdraw.data.Clip;
import jdraw.data.Frame;
import jdraw.gui.undo.DrawClip;
import jdraw.gui.undo.UndoManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.border.LineBorder;

import util.Assert;
import util.Log;
import util.Util;
import util.gui.GUIUtil;

/*
 * ClipPanel.java - created on 23.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class ClipPanel extends FloatingClip implements GridListener {

	private static final int CHANGE_NORTH = 1;
	private static final int CHANGE_WEST = 2;
	private static final int CHANGE_SOUTH = 3;
	private static final int CHANGE_EAST = 4;
	private static final int MOVE = 5;

	protected static final int TOLERANCE = 2;
	private int action = 0;
	private final boolean resizable;
	private int oldGrid;	

	private final DrawPanel drawPanel;

	private boolean mousePressed = false;
	private Point start;
	private Rectangle oldRect = null;

	public ClipPanel(boolean isResizable) {
		super(DrawLayers.CLIP_TOOL_LAYER);
		resizable = isResizable;
		setLayout(new BorderLayout(0, 0));
		drawPanel = new DrawPanel();
		add(drawPanel, BorderLayout.CENTER);
	}

	public String getName() {
		return "Clip";
	}

	public final Dimension getPreferredSize() {
		return drawPanel.getPreferredSize();
	}

	protected void activate() {
		super.activate();
		if (isActive()) {
			DrawAction.getAction(CropAction.class).setEnabled(true);
		}
	}

	protected void deactivate() {
		super.deactivate();
		if (!isActive()) {			
			DrawAction.getAction(CropAction.class).setEnabled(false);
		}
	}

	public void gridChanged(int oldValue, int newValue) {		
		if (isActive()) {			
			Rectangle r = getBounds();
			int x = r.x / oldGrid;
			int y = r.y / oldGrid;
			int w = r.width / oldGrid;
			int h = r.height / oldGrid;
			r = Tool.getDrawBounds(x, y, w, h);
			oldGrid = Tool.getGrid();
			setBounds(r);
		}
	}

	private boolean inClip(MouseEvent e) {
		if (Tool.getRealPixel(e) == null) {
			return false;
		}
		Rectangle r = getBounds();
		r.x = r.x - TOLERANCE;
		r.y = r.y - TOLERANCE;
		r.width = r.width + (TOLERANCE * 2);
		r.height = r.height + (TOLERANCE * 2);
		return r.contains(e.getX(), e.getY());
	}

	public void mouseClicked(MouseEvent e) {
		if (isActive()) {
			if (inClip(e) && (e.getClickCount() == 2)) {
				// clip absetzen.
				Rectangle r = getBounds();
				Point p = toFramePoint(r.x, r.y);
				DrawClip dc = new DrawClip(p.x, p.y, drawPanel.getClip());
				if (dc.isValid()) {
					UndoManager.INSTANCE.addUndoable(dc);
					dc.redo();
				}
			}
			e.consume();
		}
	}

	private void paintRubberBand() {
		paintRubberBand(oldRect);
	}

	private void paintRubberBand(Rectangle rect) {
		Graphics2D g = (Graphics2D) Tool.getDrawPanel().getGraphics();
		Rectangle r = Tool.getDrawBounds(rect);
		g.setXORMode(Color.white);
		g.setColor(Color.darkGray);
		g.drawRect(r.x, r.y, r.width, r.height);
		g.setPaintMode();
	}

	public void mouseDragged(MouseEvent e) {
		if (!(isActive() && mousePressed)) {
			return;
		}
		e.consume();
		if (action == MOVE) {
			moveClip(e);
		}
		else if (resizable) {
			if (oldRect == null) {
				oldRect = Tool.getRealBounds(getBounds());
				paintRubberBand();
			}
			switch (action) {
				case CHANGE_NORTH :
					changeNorth(e);
					break;
				case CHANGE_WEST :
					changeWest(e);
					break;
				case CHANGE_SOUTH :
					changeSouth(e);
					break;
				case CHANGE_EAST :
					changeEast(e);
					break;
				default :
					Assert.fail("Cannot handle action " + action);
			}
		}
	}

	public void mouseExited(MouseEvent e) {
		if (isActive()) {
			if (!mousePressed) {
				MainFrame.INSTANCE.setCursor(Cursor.DEFAULT_CURSOR);
			}
			e.consume();
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (isActive()) {
			e.consume();
			int x = e.getX();
			int y = e.getY();
			Rectangle r = getBounds();
			final int minX = r.x;
			final int maxX = r.x + r.width - 1;
			final int minY = r.y;
			final int maxY = r.y + r.height - 1;
			action = -1;
			if (resizable && inClip(e)) {
				if (Util.isIn(x, minX - TOLERANCE, minX)
					&& Util.isIn(y, minY, maxY)) {
					MainFrame.INSTANCE.setCursor(Cursor.W_RESIZE_CURSOR);
					action = CHANGE_WEST;
				}
				else if (Util.isIn(x, maxX, maxX + TOLERANCE)) {
					MainFrame.INSTANCE.setCursor(Cursor.E_RESIZE_CURSOR);
					action = CHANGE_EAST;
				}
				else if (
					Util.isIn(y, minY - TOLERANCE, minY)
						&& Util.isIn(x, minX, maxX)) {
					MainFrame.INSTANCE.setCursor(Cursor.N_RESIZE_CURSOR);
					action = CHANGE_NORTH;
				}
				else if (
					Util.isIn(y, maxY, maxY + TOLERANCE)
						&& Util.isIn(x, minX, maxX)) {
					MainFrame.INSTANCE.setCursor(Cursor.S_RESIZE_CURSOR);
					action = CHANGE_SOUTH;
				}
			}
			if (action == -1) {
				if (inClip(e)) {
					MainFrame.INSTANCE.setCursor(Cursor.MOVE_CURSOR);
					action = MOVE;
				}
				else {
					MainFrame.INSTANCE.setCursor(Cursor.DEFAULT_CURSOR);
					action = 0;
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isActive() && inClip(e)) {
			start = Tool.getRealPixel(e);
			mousePressed = true;
			e.consume();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (mousePressed) {
			e.consume();
			mousePressed = false;
			MainFrame.INSTANCE.setCursor(Cursor.DEFAULT_CURSOR);
			if (oldRect != null) {
				paintRubberBand();
				if (RectangularSelectionTool.isValidSelection(oldRect)) {
					Clip clip =
						Tool.getCurrentFrame().createClip(
							oldRect.x,
							oldRect.y,
							oldRect.width,
							oldRect.height);
					setClip(oldRect.x, oldRect.y, clip);
				}

				oldRect = null;
			}
		}
	}

	private void moveClip(MouseEvent e) {
		Point current = Tool.getRealPixel(e);
		if ((current != null) && (!current.equals(start))) {
			final int grid = Tool.getGrid();
			final int xDiff = (current.x - start.x) * grid;
			final int yDiff = (current.y - start.y) * grid;
			Rectangle r = getBounds();
			r.x = r.x + xDiff;
			r.y = r.y + yDiff;
			setBounds(r);
			start = current;
		}
	}

	private void changeNorth(MouseEvent e) {
		Point current = Tool.getRealPixel(e);
		if ((current != null) && (current.y != oldRect.y)) {
			paintRubberBand();
			Rectangle bounds = Tool.getRealBounds(getBounds());
			int height = (bounds.height + (bounds.y - current.y));
			oldRect = new Rectangle(bounds.x, current.y, bounds.width, height);
			paintRubberBand();
		}
	}

	private void changeSouth(MouseEvent e) {
		Point current = Tool.getRealPixel(e);
		if ((current != null) && (current.y != oldRect.y + oldRect.height - 1)) {
			paintRubberBand();
			Rectangle bounds = Tool.getRealBounds(getBounds());
			int height = (current.y - bounds.y) + 1;
			oldRect = new Rectangle(bounds.x, bounds.y, bounds.width, height);
			paintRubberBand();
		}
	}

	private void changeWest(MouseEvent e) {
		Point current = Tool.getRealPixel(e);
		if ((current != null) && (current.x != oldRect.x)) {
			paintRubberBand();
			Rectangle bounds = Tool.getRealBounds(getBounds());
			int width = (bounds.width + (bounds.x - current.x));
			oldRect = new Rectangle(current.x, bounds.y, width, bounds.height);
			paintRubberBand();
		}
	}

	private void changeEast(MouseEvent e) {
		Point current = Tool.getRealPixel(e);
		if ((current != null) && (current.x != oldRect.x + oldRect.width - 1)) {
			paintRubberBand();
			Rectangle bounds = Tool.getRealBounds(getBounds());
			int width = (current.x - bounds.x) + 1;
			oldRect = new Rectangle(bounds.x, bounds.y, width, bounds.height);
			paintRubberBand();
		}
	}

	

	public void paint(Graphics g) {
		if (isActive()) {
			super.paint(g);
			Dimension dim = getSize();			
			g.setColor(Color.red);
			g.drawRect(0, 0, dim.width - 1, dim.height - 1);
			g.setColor(Color.white);
			g.drawRect(1, 1, dim.width - 3, dim.height - 3);
			g.setColor(Color.black);
			g.drawRect(2, 2, dim.width - 5, dim.height - 5);
		}
	}

	protected void setClip(int x, int y, Clip aClip) {
		drawPanel.setClip(aClip);		
		Rectangle r =
			Tool.getDrawBounds(x, y, aClip.getWidth(), aClip.getHeight());
		defineClip(r.x, r.y, r.width, r.height);
		oldGrid = Tool.getGrid();
	}
}
