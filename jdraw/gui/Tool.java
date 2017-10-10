package jdraw.gui;

import jdraw.action.DrawAction;
import jdraw.action.ToggleAntialiasAction;
import jdraw.action.ToggleGradientFillAction;
import jdraw.data.Clip;
import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import util.Assert;
import util.Log;
import util.Util;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public abstract class Tool implements KeyEventDispatcher, DrawMouseListener {
	private static final int CLICKED = 5;
	
	//variabile per strumento maglia
	private boolean strumentoMaglia;
	private String magliaSelezionata;

	// mouse event konstanten
	protected static final int DRAG_LEFT = 16;
	protected static final int DRAG_MIDDLE = 8;
	protected static final int DRAG_RIGHT = 4;
	private static final int DRAGGED = 3;
	private static final int ENTERED = 0;
	private static final int EXITED = 1;

	// die von den tools erwartete button konstanten
	public static final int LEFT_BUTTON = 0;
	private static final int MOVED = 4;

	protected static final int NO_BUTTON = 2;

	private static final int NONE = -1;
	private static final int PRESSED = 2;
	private static final int RELEASED = 6;
	public static final int RIGHT_BUTTON = 1;
	private boolean isDragged = false;
	protected int lastEventType = NONE;

	protected int lastModifiers = 0;
	protected Point lastPoint = null;

	private boolean swallowKeys = false;
	protected boolean isActive = false;

	protected static final void drawInfo(Point p) {
		if (p == null) {
			StatusPanel.INSTANCE.drawInfo(null);
		}
		else {
			StringBuffer buf;
			int larghezzaDisegno=Clip.getLarghezzaDisegno();
			if(p.x<larghezzaDisegno) {
			 buf = new StringBuffer("( R:");
			buf.append(p.y+1);
			buf.append(", C: ");
			buf.append(p.x+1);
			buf.append(") #");
			buf.append(getCurrentFrame().getPixel(p.x, p.y));
			}
			else {
				 buf = new StringBuffer(" Descrivere il tipo di comando");
			}
			StatusPanel.INSTANCE.drawInfo(buf.toString());
		}
	}

	protected static final int getButton(int b) {
		switch (b) {
			case MouseEvent.BUTTON1 :
			case DRAG_LEFT :
				return LEFT_BUTTON;
			case MouseEvent.BUTTON2 :
			case MouseEvent.BUTTON3 :
			case DRAG_RIGHT :
			case DRAG_MIDDLE :
				return RIGHT_BUTTON;
			default :
				return NO_BUTTON;

		}
	}

	public static final PreviewPanel getPreview() {
		return PreviewPanel.INSTANCE;
	}

	public static final Frame getCurrentFrame() {
		return getPicture().getCurrentFrame();
	}

	public static final FramePanel getCurrentFramePanel() {
		return FolderPanel.INSTANCE.getCurrentFramePanel();
	}

	public static final Palette getCurrentPalette() {
		return getCurrentFrame().getPalette();
	}

	public static final Tool getCurrentTool() {
		return ToolPanel.INSTANCE.getCurrentTool();
	}
	
	public static final String getTipoLavoro() {
		return ToolPanel.INSTANCE.getMagliaSelezionata();
	}

	protected static final Rectangle getDrawBounds(int x, int y, int w, int h) {
		final int grid = getGrid();
		x = x * grid;
		y = y * grid;
		w = w * grid;
		h = h * grid;
		return new Rectangle(x, y, w, h);
	}

	protected static final Rectangle getDrawBounds(Rectangle r) {
		return getDrawBounds(r.x, r.y, r.width, r.height);
	}

	public static final DrawPanel getDrawPanel() {
		return getCurrentFramePanel().getDrawPanel();
	}

	protected static final Point getDrawPixel(int x, int y) {
		//		Rectangle r = Tool.getCurrentFramePanel().getBounds();
		final int grid = getGrid();
		x = (x * grid); // + r.x;
		y = (y * grid); // + r.y;
		return new Point(x, y);
	}

	public static final int getPictureHeight() {
		return getPicture().getHeight();
	}

	public static final int getPictureWidth() {
		return getPicture().getWidth();
	}

	public static final Dimension getPictureDimension() {
		return new Dimension(getPictureWidth(), getPictureHeight());
	}

	public static final int getGrid() {
		return FolderPanel.getGrid();
	}

	public static final Picture getPicture() {
		return MainFrame.INSTANCE.getPicture();
	}

	public static final Rectangle getRealBounds(int x, int y, int w, int h) {
		final int grid = getGrid();
		x = x / grid;
		y = y / grid;
		w = w / grid;
		h = h / grid;
		return new Rectangle(x, y, w, h);
	}

	public static final Rectangle getRealBounds(Rectangle r) {
		return getRealBounds(r.x, r.y, r.width, r.height);
	}

	public static final Point getRealPixel(int x, int y) {
		final int grid = getGrid();
		final int maxX = getPictureWidth()-1;
		final int maxY = getPictureHeight()-1;
		x = ((x+(grid/2)) / grid) ;		//  valori corretti x avere indici che partono dal basso a sx
		y = maxY-(y / grid);

		if ((x < 0) || (y < 0) || (x > (maxX+DrawPanel.NUMERO_COMANDI)) || (y > maxY) || (x==maxX+1)) { // aggiungo extra x gestire i comandi macchina
			return null;
		}
		return new Point(x, y);
	}

	protected static final Point getRealPixel(MouseEvent e) {
		return getRealPixel(e.getX(), e.getY());
	}

	public static final Point getViewPoint() {
		return getCurrentFramePanel()
			.getScrollPane()
			.getViewport()
			.getViewPosition();
	}

	public static final boolean isAntialiasOn() {
		boolean supported =
			ToolPanel.INSTANCE.getCurrentTool().supportsAntialias();

		ToggleAntialiasAction action =
			(ToggleAntialiasAction) DrawAction.getAction(
				ToggleAntialiasAction.class);
		return supported && action.antialiasOn();
	}

	public static final boolean isGradientFillOn() {
		boolean supported =
			ToolPanel.INSTANCE.getCurrentTool().supportsGradientFill();

		ToggleGradientFillAction action =
			(ToggleGradientFillAction) DrawAction.getAction(
				ToggleGradientFillAction.class);
		return supported && action.gradientFillOn();
	}

	public void activate() {
		if (!isActive) {
			if (Log.DEBUG)
				Log.debug("- activated " + Util.shortClassName(this.getClass()));
			isActive = true;
			KeyboardFocusManager
				.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(
				this);
			attach();
		}
	}

	protected final void attach() {
		lastPoint = null;
	}
	protected void clicked(int button, Point p) {
	}

	public void deactivate() {
		if (isActive) {
			if (Log.DEBUG)
				Log.debug("- deactivated " + Util.shortClassName(this.getClass()));
			isActive = false;
			KeyboardFocusManager
				.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(
				this);
			detach();
		}
	}

	protected final void detach() {
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		return swallowKeys;
	}

	protected void entered(int button, Point p) {
	}
	protected void exited(int button, Point p) {
	}

	public boolean isDragged() {
		return isDragged;
	}

	// nur einzelne gedr�ckte maustasten erlauben
	private boolean isValidMousePressing(MouseEvent e) {
		return (lastModifiers == 0)
			&& ((e.getModifiers() == MouseEvent.BUTTON1_MASK)
				|| (e.getModifiers() == MouseEvent.BUTTON2_MASK)
				|| (e.getModifiers() == MouseEvent.BUTTON3_MASK));
	}

	public final void mouseClicked(MouseEvent e) {
		isDragged = false;
		Point p = getRealPixel(e);
		int button = getButton(e.getButton());
		if (!skipPoint(CLICKED, p)) {
			drawInfo(p); // stampo le coordinate
			clicked(button, p);
		}
	}

	public final void mouseDragged(MouseEvent e) {
		isDragged = true;
		Point p = getRealPixel(e);
		
		if(p.y == 7)
			System.out.println("raff_drag");
		int button = getButton(e.getModifiers());
		//if (!skipPoint(DRAGGED, p)) {
			if (p != null) {
				drawInfo(p);
			}
			pressed(button, p);
		//}
	}

	//	fuer alle mouse event werden zunaechst die koordinaten umgesetzt

	public final void mouseEntered(MouseEvent e) {
		Point p = getRealPixel(e);
		int button = getButton(e.getButton());
		if (!skipPoint(ENTERED, p)) {
			drawInfo(p);
			entered(button, p);
		}
	}

	public final void mouseExited(MouseEvent e) {
		Point p = getRealPixel(e);
		int button = getButton(e.getButton());
		if (!skipPoint(EXITED, p)) {
			drawInfo(p);
			exited(button, p);
		}
	}

	public final void mouseMoved(MouseEvent e) {
		isDragged = false;
		Point p = getRealPixel(e);
		int button = getButton(e.getButton());
		if (!skipPoint(MOVED, p)) {
			drawInfo(p);
			moved(button, p);
		}
	}

	public final void mousePressed(MouseEvent e) {
		if (isValidMousePressing(e)) {
			lastModifiers = e.getModifiers();
			isDragged = false;
			Point p = getRealPixel(e);
			int button = getButton(e.getButton());
			if (!skipPoint(PRESSED, p)) {
				drawInfo(p);
				pressed(button, p);
			}
		}
	}

	public final void mouseReleased(MouseEvent e) {
		if ((e.getModifiers() & lastModifiers) > 0) {
			lastModifiers = 0;
			isDragged = false;
			Point p = getRealPixel(e);
			int button = getButton(e.getButton());
			if (!skipPoint(RELEASED, p)) {
				drawInfo(p);
				released(button, p);
			}
		}
	}
	protected void moved(int button, Point p) {
	}
	protected void pressed(int button, Point p) {
	}
	protected void released(int button, Point p) {
	}

	public void setSwallowKeys(boolean b) {
		swallowKeys = b;
	}

	private boolean skipPoint(int eventType, Point p) {
		boolean skip = true;
		if (lastEventType != eventType) {
			skip = false;
		}
		else if ((p != null) && (!p.equals(lastPoint))) {
			skip = false;
		}
		else if ((lastPoint != null) && (!lastPoint.equals(p))) {
			skip = false;
		}
		lastEventType = eventType;
		lastPoint = p;

		return skip;
	}

	public boolean supportsAntialias() {
		return false;
	}

	public boolean supportsGradientFill() {
		return false;
	}

	public  boolean isStrumentoMaglia() {
		return strumentoMaglia;
	}

	public  void setStrumentoMaglia(boolean strumentoMaglia) {
		this.strumentoMaglia = strumentoMaglia;
	}

	public String getMagliaSelezionata() {
		return magliaSelezionata;
	}

	public void setMagliaSelezionata(String magliaSelezionata) {
		this.magliaSelezionata = magliaSelezionata;
	}
	
	
	

}
