package jdraw.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.Assert;
import util.Log;

/*
 * MouseHandler.java - created on 22.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class MouseHandler
	implements MouseMotionListener, MouseListener {

	public static final MouseHandler INSTANCE = new MouseHandler();
	private FramePanel framePanel = null;

	private final TreeSet layerKeys = new TreeSet(new LayoutComparator());

	private final HashMap clipMap = new HashMap();

	private MouseHandler() {
	}

	public void addClip(FloatingClip clip) {
		Assert.isFalse(
			layerKeys.contains(clip.getLayer()),
			"gui: multiple clips for layer "
				+ String.valueOf(clip.getLayer().intValue()));
		layerKeys.add(clip.getLayer());
		clipMap.put(clip.getLayer(), clip);
		if (MainFrame.INSTANCE != null) {
			Tool.getCurrentFramePanel().addClip(clip);
		}
	}

	private void addClips() {
		Iterator clips = layerKeys.iterator();
		FramePanel p = Tool.getCurrentFramePanel();
		while (clips.hasNext()) {
			p.addClip(getClip(clips.next()));
		}
	}

	private void hideClips() {
		Iterator clips = layerKeys.iterator();
		while (clips.hasNext()) {
			framePanel.removeClip(getClip(clips.next()));
		}
	}

	public void frameChanged() {
		if (framePanel != null) {
			framePanel.getDrawPanel().removeMouseListener(this);
			framePanel.getDrawPanel().removeMouseMotionListener(this);			
			hideClips();
		}
		framePanel = Tool.getCurrentFramePanel();
		addClips();
		framePanel.getDrawPanel().addMouseListener(this);
		framePanel.getDrawPanel().addMouseMotionListener(this);		
		InfoClip.INSTANCE.repeat();
		Tool.getCurrentFramePanel().revalidateFrame();
	}

	public void deleteClip(FloatingClip aClip) {
		Assert.isTrue(
			layerKeys.contains(aClip.getLayer()),
			"gui: floating clip for layer "
				+ String.valueOf(aClip.getLayer().intValue())
				+ "not found");
		layerKeys.remove(aClip.getLayer());
		clipMap.remove(aClip.getLayer());
		Tool.getCurrentFramePanel().getLayeredPane().remove(aClip);
	}

	private FloatingClip getClip(Object key) {
		return (FloatingClip) clipMap.get(key);
	}

	public void mouseDragged(MouseEvent e) {
		InfoClip.INSTANCE.mouseDragged(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext() && (!e.isConsumed())) {
			clip = getClip(layers.next());
			clip.mouseDragged(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseDragged(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
		InfoClip.INSTANCE.mouseMoved(e);
		Iterator layers = layerKeys.iterator();
		Tool.getCurrentTool().mouseMoved(e);
		FloatingClip clip;
		while (layers.hasNext() && (!e.isConsumed())) {
			clip = getClip(layers.next());
			clip.mouseMoved(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseMoved(e);
		}
	}

	public void mouseClicked(MouseEvent e) {
		InfoClip.INSTANCE.mouseClicked(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext()) {
			clip = getClip(layers.next());
			clip.mouseClicked(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseClicked(e);
		}
	}

	public void mouseEntered(MouseEvent e) { // stampa la posizione del mouse
		InfoClip.INSTANCE.mouseEntered(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext()) {
			clip = getClip(layers.next());
			clip.mouseEntered(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseEntered(e);
		}
	}

	public void mouseExited(MouseEvent e) {
		InfoClip.INSTANCE.mouseExited(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext()) {
			clip = getClip(layers.next());
			clip.mouseExited(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseExited(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		InfoClip.INSTANCE.mousePressed(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext()) {
			clip = getClip(layers.next());
			clip.mousePressed(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		InfoClip.INSTANCE.mouseReleased(e);
		Iterator layers = layerKeys.iterator();
		FloatingClip clip;
		while (layers.hasNext()) {
			clip = getClip(layers.next());
			clip.mouseReleased(e);
		}
		if (!e.isConsumed()) {
			Tool.getCurrentTool().mouseReleased(e);
		}
	}

	// layer comparator
	private final class LayoutComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			Integer a = (Integer) o1;
			Integer b = (Integer) o2;			
			return -a.compareTo(b);
		}
	}


}
