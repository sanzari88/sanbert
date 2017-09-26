package jdraw.gui.undo;

import java.awt.Point;

import jdraw.data.Clip;
import jdraw.data.Pixel;

/*
 * DrawPixel.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public final class DrawPixel extends Undoable {

	private final Pixel[] pixels;

	public DrawPixel(Clip clip, Pixel[] pix) {
		super(clip);
		pixels = pix;
	}

	public DrawPixel(Pixel[] pix) {
		pixels = pix;
	}

	public DrawPixel(int x, int y, int col) {
		pixels = new Pixel[1];
		int oldCol = frame.getPixel(x, y);
		pixels[0] = new Pixel(x, y, oldCol, col);
	}

	public DrawPixel(Point[] points, int col, int[] oldColours) {
		final int len = points.length;
		pixels = new Pixel[len];
		Point p;
		for (int i = 0; i < len; i++) {
			p = points[i];
			pixels[i] = new Pixel(p.x, p.y, oldColours[i], col);
		}
	}

	public void redo() {
		frame.setPixels(pixels);
	}

	public void undo() {
		frame.restorePixels(pixels);
	}

}
