package jdraw.gui;

import jdraw.data.ColourEntry;
import jdraw.data.Picture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import util.Assert;
import util.ResourceLoader;
import util.gui.BackgroundPanel;
import util.gui.GUIUtil;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public class ColourPanel extends BackgroundPanel {

	protected static final Dimension DIMENSION = new Dimension(24, 24);

	protected static final Border PLAIN_BORDER =
		new CompoundBorder(
			new LineBorder(Color.darkGray),
			new LineBorder(Color.black));

	private static final Border PICTURE_BACKGROUND_BORDER =
		new CompoundBorder(
			new LineBorder(Color.white),
			new LineBorder(Color.black));

	private boolean isForeground = false;
	private boolean isBackground = false;
	private boolean isTransparent = false;
	private boolean isPictureBackground = false;

	protected final int index;

	public ColourPanel(ColourEntry col) {
		setBorder(PLAIN_BORDER);

		index = col.getIndex();
		setBackgroundIcon(
			ResourceLoader.getImage("jdraw/images/background.gif"));
		update();
	}

	public Dimension getPreferredSize() {
		return DIMENSION;
	}

	public void update() {
		Picture pic = Tool.getPicture();
		Color col = pic.getCurrentPalette().getColour(index).getColour();

		setForeground(col);
		isForeground = (index == pic.getForeground());
		isBackground = (index == pic.getBackground());
		isTransparent = (index == pic.getTransparent());
		isPictureBackground = (index == pic.getPictureBackground());
		if (isPictureBackground) {
			setBorder(PICTURE_BACKGROUND_BORDER);
		}
		else {
			setBorder(PLAIN_BORDER);
		}
		updateToolTipText();
		Assert.isTrue(
			(col.getAlpha() != ColourEntry.TRANSPARENT) || (isTransparent),
			"Duplicate full transparent colour found: #" + index);

		repaint();
	}

	private void updateToolTipText() {
		StringBuffer buf = new StringBuffer();
		ColourEntry e = Tool.getCurrentPalette().getColour(index);
		final int alpha = e.getColour().getAlpha();

		if (isTransparent) {
			buf.append("Transparent - ");
		}
		if (alpha != ColourEntry.OPAQUE) {
			buf.append("Alpha:");
			buf.append(alpha);
			buf.append(" - ");
		}
		if (isPictureBackground) {
			buf.append("Picture background - ");
		}
		if (isForeground) {
			buf.append("Foreground - ");
		}
		if (isBackground) {
			buf.append("Background - ");
		}
		String s = buf.toString().trim();
		if (s.length() > 0) {
			s = s.substring(0, s.length() - 2);
			setToolTipText(s);
		}
		else {
			setToolTipText(null);
		}
	}

	public void setBackground(boolean b) {
		isBackground = b;
		repaint();
	}

	public void setForeground(boolean b) {
		isForeground = b;
		repaint();
	}

	public void setTransparent(boolean b) {
		isTransparent = b;
		repaint();
	}

	private Color getOpaqueColour() {
		Color c = getForeground();
		if (c.getAlpha() == ColourEntry.OPAQUE) {
			return c;
		}
		return new Color(c.getRed(), c.getGreen(), c.getBlue());
	}

	public void paint(Graphics gr) {
		Graphics2D g = GUIUtil.createGraphics(gr);
		super.paint(g);
		
		Border b = getBorder();
		Insets insets = b.getBorderInsets(this);
		Dimension dim = getSize();
		final int minX = insets.left;
		final int minY = insets.top;
		final int maxX =
			(dim.width - minX - (insets.right - 1) - (insets.left - 1));
		final int maxY =
			(dim.height - minY - (insets.bottom - 1) - (insets.top - 1));

		if (isTransparent) { // transparente farbe: schwarzes dreieck oben			
			g.setColor(Color.black);
		}
		else { // bunt (oder semi-transparent)

			if (getForeground().getAlpha() == ColourEntry.OPAQUE) {
				g.setColor(getForeground());
				g.fillRect(minX, minY, maxX, maxY);
			}
			else {
				Shape oldShape = g.getClip();
				
				Polygon shape = new Polygon();
				shape.addPoint(maxX + 2, minY);
				shape.addPoint(maxX + 2, maxY + 2);
				shape.addPoint(minX, maxY + 2);

				g.setClip(shape);

				g.setColor(getForeground());
				g.fillRect(minX, minY, maxX, maxY);

				g.setClip(oldShape);
				g.setColor(Color.white);
				g.drawLine(maxX, minY, minX, maxY);
				g.setColor(Color.black);
				g.drawLine(maxX + 1, minY, minX, maxY + 1);
			}
		}

		final int w = 6;
		if (isForeground) {
			g.setColor(Color.white);
			g.fillRect(1, 1, w, w);
			g.setColor(Color.black);
			g.drawRect(1, 1, w, w);
		}
		if (isBackground) {
			g.setColor(Color.black);
			g.fillRect(DIMENSION.width - w - 3, DIMENSION.height - w - 3, w, w);
			g.setColor(Color.white);
			g.drawRect(DIMENSION.width - w - 3, DIMENSION.height - w - 3, w, w);
		}
	}

}
