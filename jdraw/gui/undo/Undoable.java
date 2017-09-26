package jdraw.gui.undo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;

import jdraw.data.Clip;
import jdraw.data.Palette;
import jdraw.data.Pixel;
import jdraw.gui.Tool;
import util.Log;

/*
 * Undoable.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public abstract class Undoable {

	protected final Clip frame;
	public abstract void redo();
	public abstract void undo();

	public Undoable() {
		this(Tool.getCurrentFrame());
	}

	public Undoable(Clip aFrame) {
		frame = aFrame;
	}

	public final Clip getFrame() {
		return frame;
	}

	protected final void prepareGraphics(Graphics g) {
		if (Tool.isAntialiasOn()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}

	public boolean isValid() {
		return true;
	}

	public static final DrawPixel calculateDifference(
		Clip aClip,
		Image img,
		int minX,
		int minY,
		int w,
		int h) {
		PixelGrabber grabber = new PixelGrabber(img, 0, 0, w, h, true);
		try {
			if (!grabber.grabPixels()) {
				if (Log.DEBUG)
					Log.debug("undo: grabbing pixels failed");
				return null;
			}
		}
		catch (InterruptedException e) {
			Log.exception(e);
			return null;
		}

		ArrayList list = new ArrayList();
		int[] grabbedPixels = (int[]) grabber.getPixels();
		int i = 0;
		int newColour;
		int oldColour;
		for (int y = minY; y < minY + h; y++) {
			for (int x = minX; x < minX + w; x++) {
				oldColour = aClip.getPixel(x, y);
				newColour = indexOfPixelColour(grabbedPixels[i]);
				if (oldColour != newColour) {
					list.add(new Pixel(x, y, oldColour, newColour));
				}
				i++;
			}
		}
		final int size = list.size();
		if (size == 0) {
			Log.debug("undo: no pixels changed");
			return null;
		}
		Pixel[] pixels = new Pixel[size];
		list.toArray(pixels);
		return new DrawPixel(aClip, pixels);
	}

	private static final int indexOfPixelColour(int pixel) {
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		Color c = new Color(red, green, blue);
		Palette pal = Tool.getCurrentPalette();
		int index = pal.findColour(c);
		if (index == -1) {
			index = pal.size();
			pal.addColour(c);
		}
		return index;
	}
}
