package jdraw.gui;

import java.awt.Point;
import java.util.ArrayList;

import jdraw.data.Clip;
import jdraw.data.Frame;
import jdraw.gui.undo.DrawPixel;
import jdraw.gui.undo.UndoManager;
import util.Assert;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class PixelTool extends Tool {

	public static final PixelTool INSTANCE = new PixelTool();

	private int lastButton;

	private ArrayList pixels = new ArrayList();
	private ArrayList oldColours = new ArrayList();

	private int currentColour;

	private PixelTool() {
	}

	public final void deactivate() {
		super.deactivate();
		pixels.clear();
		oldColours.clear();
	}

	public void pressed(int button, Point p) { // Cattura la pressione del mouse
		lastButton = button;
		if (p != null) {

			switch (button) {
				case LEFT_BUTTON :
					setPixel(p, Tool.getPicture().getForeground());
					break;
				case RIGHT_BUTTON :
					setPixel(p, Tool.getPicture().getBackground());
					break;
				default :
					Assert.fail("gui: unknown button " + button);
			}
		}
	}

	private void setPixel(Point p, int colour) {
		Frame frame = getCurrentFrame();
		if(p.y==4)
			System.out.println("raffo");;
		int y_Invertita=Clip.getYInvertita(p.y);
		currentColour = colour;
		int oldColour = frame.getPixel(p.x,y_Invertita);
			p.setLocation(p.x, y_Invertita);
			pixels.add(p);
			oldColours.add(new Integer(oldColour));
			frame.setPixel(p.x, y_Invertita, colour);
	}

	public void released(int button, Point p) {
		final int size = pixels.size();
		if (size > 0) {
			Point[] points = new Point[size];
			pixels.toArray(points);

			int[] oldCols = new int[size];
			for (int i = 0; i < size; i++) {
				oldCols[i] = ((Integer) oldColours.get(i)).intValue();
			}
			DrawPixel dp = new DrawPixel(points, currentColour, oldCols);
			UndoManager.INSTANCE.addUndoable(dp);

			pixels.clear();
			oldColours.clear();
		}
	}

}
