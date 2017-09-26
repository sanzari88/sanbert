package jdraw.gui.undo;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.FolderPanel;
import jdraw.gui.GradientPanel;
import jdraw.gui.Tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/*
 * DrawOval.java - created on 14.11.2003
 * 
 * @author Michaela Behling
 */

public final class DrawOval extends Undoable {

	private final DrawPixel draw;

	public DrawOval(int mouseButton, boolean doFill, Point start, Point end) {
		Color col;
		Palette pal = Tool.getCurrentPalette();
		Picture pic = Tool.getPicture();
		if (mouseButton == Tool.LEFT_BUTTON) {
			col = pal.getColour(pic.getForeground()).getColour();
		}
		else {
			col = pal.getColour(pic.getBackground()).getColour();
		}

		draw = savePixels(col, doFill, start, end);
	}

	public boolean isValid() {
		return draw != null;
	}

	private DrawPixel savePixels(
		Color col,
		boolean doFill,
		Point start,
		Point end) {
		int minX = Math.min(start.x, end.x);
		int maxX = Math.max(start.x, end.x);
		int minY = Math.min(start.y, end.y);
		int maxY = Math.max(start.y, end.y);
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

		if (Tool.isGradientFillOn()) {
			((Graphics2D) g).setPaint(
				GradientPanel.INSTANCE.createGradient(
					new Rectangle(minX, minY, w, h)));
		}
		else {
			g.setColor(col);
		}
		int ww = w - 1;
		int hh = h - 1;

		if (doFill) {
			g.fillOval(minX, minY, ww, hh);
		}
		else {
			g.drawOval(minX, minY, ww, hh);
		}

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
