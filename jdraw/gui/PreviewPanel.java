package jdraw.gui;

import jdraw.data.DataChangeListener;
import jdraw.data.Frame;
import jdraw.data.event.ChangeEvent;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.event.ChangeListener;

import util.Assert;
import util.Log;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public final class PreviewPanel extends DrawPanel implements ChangeListener {

	public static final PreviewPanel INSTANCE = new PreviewPanel();

	private FramePanel framePanel;

	private PreviewPanel() {
		setOpaque(true);
	}

	public int getGrid() {
		return 1;
	}

	private void detach() {
		if ( framePanel !=  null ) {
			framePanel.getScrollPane().getViewport().removeChangeListener(this);
		}
	}
	
	private void attach() {
		framePanel.getScrollPane().getViewport().addChangeListener(this);
	}

	protected void setClip(Frame aFrame) {		
		detach();
		clip = aFrame;
		framePanel = Tool.getCurrentFramePanel();
		attach();
		setViewpointFromFramePanel();
		getParentViewport().revalidate();
	}

	public boolean showGrid() {
		return false;
	}

	private JViewport getParentViewport() {
		return (JViewport) getParent();
	}


	public final Dimension getPreferredSize() {
		if (clip == null) {
			return super.getPreferredSize();
		}
		return new Dimension(clip.getWidth(), clip.getHeight());
	}

	private void setViewpointFromFramePanel() {
		JViewport port = framePanel.getScrollPane().getViewport();
		Point p = port.getViewPosition();
		final int grid = Tool.getGrid();
		p.x = p.x / grid;
		p.y = p.y / grid;

		Dimension dim = getPreferredSize();
		JViewport parent = getParentViewport();
		Dimension extent = parent.getExtentSize();

		if (extent.width >= dim.width) {
			p.x = 0;
		}
		else if (((p.x + extent.width) - 1) > dim.width) {
			p.x = dim.width - extent.width;
		}
		if (extent.height >= dim.height) {
			p.y = 0;
		}
		else if (((p.y + extent.height) - 1) > dim.height) {
			p.y = dim.height - extent.height;
		}
		parent.setViewPosition(p);
	}

	public void stateChanged(javax.swing.event.ChangeEvent e) {
		setViewpointFromFramePanel();
	}

}