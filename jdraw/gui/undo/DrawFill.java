package jdraw.gui.undo;

import jdraw.data.Clip;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.data.Pixel;
import jdraw.gui.Tool;
import magliera.puntoMaglia.TipoLavoroEnum;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import util.Log;

/*
 * DrawFill.java - created on 15.11.2003
 * 
 * @author Michaela Behling
 */

public final class DrawFill extends Undoable {

	private final DrawPixel draw;

	public DrawFill(int mouseButton, int x, int y) {
		y=Clip.getYInvertita(y);
		int oldCol = frame.getPixel(x, y);		// invere coordinale all'interno
		Picture pic = Tool.getPicture();
		int newCol;
		if (mouseButton == Tool.LEFT_BUTTON) {
			newCol = pic.getForeground();
		}
		else {
			newCol = pic.getBackground();
		}
		draw = savePixels(x, y, oldCol, newCol);
	}

	public boolean isValid() {
		return draw != null;
	}

	private DrawPixel savePixels(int x, int y, int oldCol, int newCol) {
//		if (oldCol == newCol) {	// tolgo controllo su vecchio colore
//			return null;
//		}
		ArrayList points = new ArrayList();
		points.add(new Point(x, y));
		ArrayList pixels = new ArrayList();
		final int width = frame.getWidth();
		final int height = frame.getHeight();
		do {
			Point p = (Point) points.remove(0);
			if (!contains(pixels, p)) {
				if ((p.x >= 0) && (p.x < width) && (p.y >= 0) && (p.y < height)) {
					if (frame.getPixel(p.x, p.y) == oldCol) { // tolgo il controllo sul vecchio colore
						frame.setMaglia(p.x, p.y, newCol, jdraw.gui.Tool.getTipoLavoro());
						pixels.add(new Pixel(p.x, p.y, oldCol, newCol));

						points.add(new Point(p.x - 1, p.y));	//capire il xk fa così
						points.add(new Point(p.x + 1, p.y));
						points.add(new Point(p.x, p.y - 1));
						points.add(new Point(p.x, p.y + 1));
					}
				}
			}
		}
		while (points.size() > 0);
		final int size = pixels.size();
		if (size == 0) {
			return null;
		}
		Pixel[] pixelField = new Pixel[size];
		pixels.toArray(pixelField);

		return new DrawPixel(pixelField);
	}

	private boolean contains(ArrayList list, Point p) {
		final int size = list.size();
		Pixel pixel;
		for (int i = 0; i < size; i++) {
			pixel = (Pixel) list.get(i);
			if ((pixel.x == p.x) && (pixel.y == p.y)) {
				return true;
			}
		}
		return false;
	}

	public void redo() {
		draw.redo();
	}

	public void undo() {
		draw.undo();
	}

}
