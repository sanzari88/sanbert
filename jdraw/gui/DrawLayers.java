package jdraw.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLayeredPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import jdraw.data.Clip;
import util.Assert;
import util.Log;

/*
 * DrawLayers.java - created on 24.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class DrawLayers
	extends JLayeredPane
	implements Scrollable, GridListener, ComponentListener {

	public static final Integer DRAW_LAYER = new Integer(0);
	public static final Integer CLIP_TOOL_LAYER = new Integer(1);
	public static final Integer INFO_CLIP_LAYER = new Integer(2);

	private final DrawPanel drawPanel = new DrawPanel();

	private int unit = Tool.getGrid();
	private int blocks = 50;

	private boolean forceWidth = false;
	private boolean forceHeight = false;

	public DrawLayers() {
		setOpaque(false);
		add(drawPanel, DRAW_LAYER);
		FolderPanel.addGridListener(this);
		drawPanel.addComponentListener(this);
	}

	public void gridChanged(int oldValue, int newValue) {
		unit = newValue;
		drawPanel.setPreferredSize(drawPanel.getPreferredSize());
	}

	public ClipPanel getClipPanel() {
		Log.debug("drawlayer: "+getComponentCountInLayer(DRAW_LAYER.intValue()));
		Log.debug("cliplayer: "+getComponentCountInLayer(CLIP_TOOL_LAYER.intValue()));
		Assert.isTrue(
			getComponentCountInLayer(CLIP_TOOL_LAYER.intValue()) == 1,
			"gui: clip panel undefined");
		return (ClipPanel) getComponentsInLayer(CLIP_TOOL_LAYER.intValue())[0];
	}

	protected DrawPanel getDrawPanel() {
		return drawPanel;
	}

	public Dimension getPreferredSize() {
		return drawPanel.getPreferredSize();
	}

	protected void setDrawClip(Clip aClip) {
		drawPanel.setClip(aClip);
		Dimension dim = drawPanel.getPreferredSize();
		drawPanel.setBounds(0, 0, dim.width, dim.height);
	}

	public final Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public final int getScrollableUnitIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.VERTICAL) { // up/down
			return unit;
		}
		else { // left/right
			return unit;
		}
	}

	public final int getScrollableBlockIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.VERTICAL) { // up/down
			return blocks * unit;
		}
		else { // left/right
			return blocks * unit;
		}
	}

	public final boolean getScrollableTracksViewportHeight() {
		return forceHeight;
	}

	public final boolean getScrollableTracksViewportWidth() {
		return forceWidth;
	}

	public void componentResized(ComponentEvent e) {
		setPreferredSize(drawPanel.getPreferredSize());
		revalidate();
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

}