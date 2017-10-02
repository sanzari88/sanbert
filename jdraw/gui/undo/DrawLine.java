package jdraw.gui.undo;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.FolderPanel;
import jdraw.gui.Tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

/*
 * DrawRectangle.java - created on 14.11.2003
 * 
 * @author Michaela Behling
 */

public final class DrawLine extends Undoable {

	private final DrawPixel draw;

	public DrawLine(int mouseButton, Point start, Point end) {
		Color col;
		Picture pic = Tool.getPicture();
		Palette pal = Tool.getCurrentPalette();
		if (mouseButton == Tool.LEFT_BUTTON) {
			col = pal.getColour(pic.getForeground()).getColour();
		}
		else {
			col = pal.getColour(pic.getBackground()).getColour();
		}
		draw = savePixels(col, start, end);
	}

	public boolean isValid() {
		return draw != null;
	}

	private DrawPixel savePixels(Color col, Point start, Point end) {
		
		int startInv =util.Util.getYInvertita(start.y);
		int endInv = util.Util.getYInvertita(end.y);
		
		int minX = Math.min(start.x, end.x);
		int maxX = Math.max(start.x, end.x);
		int minY = Math.min(startInv, endInv);
		int maxY = Math.max(startInv, endInv);
		int w = (maxX - minX) + 1;
		int h = (maxY - minY) + 1;

		Image image;
		Graphics g;
		if (Tool.isAntialiasOn()) {
			image = FolderPanel.INSTANCE.createOffScreenImage();
			g = image.getGraphics();
			prepareGraphics(g);
		}
		else {
			image = FolderPanel.INSTANCE.createOffScreenImage(minX, minY, w, h);
			g = image.getGraphics();
			g.translate(-minX, -minY);
		}

		g.setColor(col);
		//g.drawLine(start.x, start.y, end.x, end.y);
		
		g.drawLine(start.x, startInv, end.x, endInv);

		if (Tool.isAntialiasOn()) {
			return calculateDifference(
				frame,
				image,
				0,
				0,
				frame.getWidth(),
				frame.getHeight());
		}
		else {
			return calculateDifference(frame, image, minX, minY, w, h);
		}
	}

	public void redo() {
		draw.redo();
	}

	public void undo() {
		draw.undo();
	}

}
