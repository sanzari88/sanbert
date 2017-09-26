package jdraw.gui;

import jdraw.data.Clip;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import util.Log;

/*
 * FloatingClip.java - created on 22.11.2003 by J-Domain
 * Liegt in der glass pane.
 * 
 * @author Michaela Behling
 */

public abstract class FloatingClip
	extends JPanel
	implements DrawMouseListener {

	private boolean isActive = false;
	private Integer layer;



	public FloatingClip(int aLayer) {
		this(new Integer(aLayer));
		setOpaque(false);
	}

	public FloatingClip(Integer aLayer) {
		layer = aLayer;
		setOpaque(false);
	}

	public Integer getLayer() {
		return layer;
	}

	public boolean isActive() {
		return isActive;
	}

	protected void activate() {
		if (!isActive) {
			isActive = true;
		}
	}

	protected void deactivate() {
		if (isActive) {
			isActive = false;			
			setBounds(-100, -100, 0, 0);
		}
	}

	public abstract String getName();

	public void defineClip(Rectangle r) {
		setBounds(r);
		activate();
	}

	public void defineClip(int x, int y, int w, int h) {
		defineClip(new Rectangle(x, y, w, h));
	}



	protected static Point toFramePoint(int x, int y) {
		final int grid = Tool.getGrid();
		x = x / grid;
		y = y / grid;
		return new Point(x, y);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void paint(Graphics g) {
		if (isActive()) {
			super.paint(g);
		}
	}

}
