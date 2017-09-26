package util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import util.Assert;
import util.Log;

/*
 * BackgroundPanel.java - created on 20.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class BackgroundPanel extends JPanel {

	private ImageIcon icon;
	private int iconWidth;
	private int iconHeight;

	private ImageObserver imageObserver = new JPanel();
	private Image image = null;
	private boolean showBackground = false;

	public BackgroundPanel(LayoutManager manager) {
		this(true, manager);
	}

	private BackgroundPanel(boolean isDoubleBuffered, LayoutManager manager) {
		if (manager != null) {
			setLayout(manager);
		}
	}

	public BackgroundPanel(boolean isDoubleBuffered) {
		this(isDoubleBuffered, null);
	}

	public BackgroundPanel() {
		this(true);
	}

	public void setBackgroundIcon(ImageIcon anIcon) {
		int w = 0;
		int h = 0;
		if (anIcon != null) {
			w = anIcon.getIconWidth();
			h = anIcon.getIconHeight();
		}
		setBackgroundIcon(anIcon, w, h);
	}

	public void setBackgroundIcon(ImageIcon anIcon, int width, int height) {
		icon = anIcon;
		showBackground = (icon != null);
		repaint();
	}

	public void showBackgroundIcon(boolean flag) {
		boolean b = (icon != null) && (flag);
		if (b != showBackground) {
			showBackground = b;
			repaint();
		}
	}

	public boolean showingBackgroundIcon() {
		return (icon != null) && showBackground;
	}

	private Image getBufferedImage() {
		if (image == null) {

			Dimension dim = getSize();
			image = createImage(dim.width, dim.height);
			final int w = icon.getIconWidth();
			final int h = icon.getIconHeight();
			int y = 0;
			Graphics g = image.getGraphics();
			while (y < dim.height) {
				int x = 0;
				while (x < dim.width) {
					g.drawImage(icon.getImage(), x, y, w, h, imageObserver);
					x = x + w;
				}
				y = y + h;
			}
		}
		return image;
	}

	public void setSize(Dimension dim) {
		image = null;
		super.setSize(dim);
	}

	public void setSize(int w, int h) {
		image = null;
		super.setSize(w, h);
	}

	public void setPreferredSize(Dimension dim) {
		image = null;
		super.setPreferredSize(dim);
	}

	public void paint(Graphics gr) {
		if (showingBackgroundIcon()) {
			Graphics g = GUIUtil.createGraphics(gr);

			Dimension dim = getSize();
			g.drawImage(
				getBufferedImage(),
				0,
				0,
				dim.width,
				dim.height,
				imageObserver);
			super.paintChildren(g);
			super.paintBorder(g);
		}
		else {
			super.paint(gr);
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		Rectangle old = getBounds();
		Rectangle r = new Rectangle(x, y, width, height);
		if (!old.equals(r)) {
			image = null;
			super.setBounds(x, y, width, height);
		}
	}

	public void setBounds(Rectangle r) {
		Rectangle old = getBounds();
		if (!old.equals(r)) {
			image = null;
			super.setBounds(r);
		}
	}

}
