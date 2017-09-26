package jdraw.gui;

import jdraw.data.Clip;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.data.Pixel;
import jdraw.gui.undo.DrawPixel;
import jdraw.gui.undo.Undoable;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;

import util.Log;
import util.gui.TextCalculator;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class TextTool extends RectangularSelectionTool {

	public static final TextTool INSTANCE = new TextTool();
	
	private final ClipPanel textPanel = new ClipPanel(false);
	private Clip clip;
	private TextCalculator calc;
	private Image img;
	private int background;

	private TextTool() {
	}

	protected void processSelection(int mouseButton) {
		final int w = calc.getTextWidth();
		final int h = calc.getTextHeight();
		// text clip kann größer sein als das bild selbst. 
		// darum wird nicht frame.createClip aufrufen!
		clip = new Clip(w, h, Tool.getPicture().getPictureBackground());
		clip.setTransparent(Tool.getPicture().getTransparent());
		clip.fill(background);
		DrawPixel pixel = Undoable.calculateDifference(clip, img, 0, 0, w, h);
		pixel.redo();
		textPanel.setClip(0, 0, clip);
	}

	public void process(Font font, String text) {
		boolean antialias = Tool.isAntialiasOn();
		calc = new TextCalculator(0, text, font, antialias);
		img =
			Tool.getDrawPanel().createImage(
				calc.getTextWidth(),
				calc.getTextHeight());
		// prepare text graphics
		Graphics2D g = (Graphics2D) img.getGraphics();
		Palette pal = Tool.getCurrentPalette();
		Picture pic = Tool.getPicture();
		background = pic.getTransparent();
		if (background == -1) {
			background = pic.getBackground();
		}
		g.setColor(pal.getColour(background).getOpaqueColour());
		g.fillRect(0, 0, calc.getTextWidth(), calc.getTextHeight());

		if (Tool.isGradientFillOn()) {
			g.setColor(pal.getColour(pic.getForeground()).getOpaqueColour());
			((Graphics2D) g).setPaint(
				GradientPanel.INSTANCE.createGradient(
					new Dimension(calc.getTextWidth(), calc.getTextHeight())));
		}
		else {
			g.setColor(pal.getColour(pic.getForeground()).getOpaqueColour());
		}

		g.setFont(font);
		if (antialias) {
			g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		// write text
		String[] lines = calc.getLines();
		int y = (int) calc.getMetrics().getAscent();
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], 0, y);
			y = y + calc.getLineHeight();
		}
		processSelection(LEFT_BUTTON);
	}

	public boolean supportsAntialias() {
		return true;
	}

	public void activate() {
		if (!isActive) {
			super.activate();
			MouseHandler.INSTANCE.addClip(textPanel);
			FolderPanel.addGridListener(textPanel);
		}
	}

	public void deactivate() {
		if (isActive) {
			super.deactivate();
			textPanel.deactivate();
			MouseHandler.INSTANCE.deleteClip(textPanel);
			MainFrame.INSTANCE.setCursor(Cursor.DEFAULT_CURSOR);
			textPanel.deactivate();
			FolderPanel.removeGridListener(textPanel);
		}
	}

	public boolean supportsGradientFill() {
		return true;
	}

}
