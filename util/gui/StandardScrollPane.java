package util.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import util.Log;

/*
 * StandardScrollPane.java - created on 19.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class StandardScrollPane
	extends JScrollPane
	implements ComponentListener {

	private int ensuredWidth = 0;
	private int ensuredHeight = 0;

	public StandardScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);		
	}

	public StandardScrollPane(Component view) {
		super(view);		
	}

	public StandardScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);		
	}

	public StandardScrollPane() {
		super();		
	}

	public void setViewportView(Component c) {
		c.addComponentListener(this);
		super.setViewportView(c);
	}

	public final void componentResized(ComponentEvent e) {		
		if ((ensuredWidth > 0) || (ensuredHeight > 0)) {
			Dimension dim = ((JComponent) e.getSource()).getSize();
			Dimension size = getSize();
			Dimension extent = getViewport().getExtentSize();
			int h = size.height;
			int w = size.width;
			Insets in = null;
			if (getBorder() != null) {
				in = getBorder().getBorderInsets(this);
			}			
			if ((ensuredHeight != 0) && (extent.height != ensuredHeight)) {
				h = ensuredHeight;
				if (in != null) { // border
					h = h + in.top + in.bottom;
				}
				if (Math.max(ensuredWidth, extent.width) < dim.width) {
					// hbar eingeblendet?				
					h = h + getHorizontalScrollBar().getHeight();
				}
			}
			if ((ensuredWidth != 0) && (extent.width != ensuredWidth)) {
				w = ensuredWidth;
				if (in != null) { // border
					w = w + in.left + in.right;
				}
				if (Math.max(ensuredHeight, extent.height) < dim.height) {
					// vbar eingeblendet?				
					w = w + getVerticalScrollBar().getWidth();
				}
			}
			if ((h != size.height) || (w != size.width)) {
				update(new Dimension(w, h));
			}
		}
	}

	private void update(Dimension dim) {
		setPreferredSize(dim);
		invalidate();
		revalidate();

		Dialog dialog =
			(Dialog) GUIUtil.findParentComponentOfClass(this, Dialog.class);
		if (dialog != null) {
			dialog.pack();
		}
		else {
			Container parent = getParent();
			if (parent instanceof JComponent) {
				((JComponent) parent).revalidate();
			}
		}
	}

	public void componentHidden(ComponentEvent e) {
	}
	public void componentMoved(ComponentEvent e) {
	}
	public void componentShown(ComponentEvent e) {
	}

	public final void setEnsuredHeight(int i) {
		if (i >= 0) {
			ensuredHeight = i;
		}
	}

	public final void setEnsuredWidth(int i) {
		if (i >= 0) {
			ensuredWidth = i;
		}
	}
}