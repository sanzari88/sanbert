package jdraw.gui;

import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.event.ChangeEvent;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import util.ResourceLoader;
import util.gui.BackgroundPanel;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public final class FramePanel extends BackgroundPanel {

	public static final boolean SHOW_PATTERN = true;
	public static final boolean SHOW_COLOUR = false;

	private static boolean transparencyMode = SHOW_PATTERN;

	private Frame frame;
	private final DrawLayers layeredPane = new DrawLayers();
	private final JScrollPane scrollPane = new JScrollPane(layeredPane);

	public FramePanel() {
		super(new BorderLayout(0, 0));
		setBackground(Color.white);

		add(scrollPane, BorderLayout.CENTER);
		setOpaque(true);
		setBackgroundIcon(
			ResourceLoader.getImage("jdraw/images/background.gif"));

		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		//addComponentListener(this);
	}

	protected void repaintFrame(int x, int y, int width, int height) {
		Tool.getDrawPanel().repaint(x, y, width, height);
	}

	protected void addClip(FloatingClip clip) {
		layeredPane.add(clip, clip.getLayer());
	}

	protected void removeClip(FloatingClip clip) {
		layeredPane.remove(clip);
	}

	protected void repaintFrame() {
		Tool.getDrawPanel().repaint();
	}

	public void changeMainBackground(ChangeEvent e) {
		setBackground(
			Tool.getCurrentPalette().getColour(e.getNewInt()).getColour());
	}

	protected void revalidateFrame() {
		setVisible(false);
		Palette p = frame.getPalette();
		Color back = scrollPane.getViewport().getBackground();
		if (frame.getTransparentColour() != -1) {
			back = p.getColour(frame.getTransparentColour()).getColour();
		}
		setBackground(back);
		invalidate();
		revalidate();
		JComponent parent = (JComponent) getParent();
		if (parent != null) {
			parent.revalidate();
		}
		setVisible(true);

		PreviewPanel.INSTANCE.setVisible(false);
		PreviewPanel.INSTANCE.revalidate();
		PreviewPanel.INSTANCE.setVisible(true);		
	}

	protected DrawPanel getDrawPanel() {
		return layeredPane.getDrawPanel();
	}

	protected int getGrid() {
		return Tool.getGrid();
	}

	protected JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void toggleTransparencyMode() {
		transparencyMode = !transparencyMode;
		showBackgroundIcon(transparencyMode == SHOW_PATTERN);
		setBackground(
			frame
				.getPalette()
				.getColour(Tool.getPicture().getPictureBackground())
				.getColour());
	}

	public boolean getTransparencyMode() {
		return transparencyMode;
	}

	public void changeClipData(ChangeEvent e) {
		if (isShowing()) {
			revalidateFrame();
		}
	}

	public void changePixel(ChangeEvent e) {
		if (isShowing()) {
			final int grid = getGrid();
			repaintFrame(e.getX() * grid, e.getY() * grid, grid, grid);
			PreviewPanel.INSTANCE.repaint(e.getX(), e.getY(), 1, 1);
		}
	}

	public void changeNeedsRepaint(ChangeEvent e) {
		if (isShowing()) {
			repaintFrame();
			PreviewPanel.INSTANCE.repaint();
		}
	}

	public void setFrame(Frame aFrame) {
		frame = aFrame;
		layeredPane.setDrawClip(frame);
		
		//revalidateFrame();
	}

	protected void transparentColourChanged() {
		int index = frame.getTransparentColour();
		Color col;
		if (index == -1) {
			col = Color.black;
		}
		else {
			col = frame.getPalette().getColour(index).getColour();
		}
		setBackground(col);
	}

	//	public void componentHidden(ComponentEvent e) {
	//	}
	//	public void componentMoved(ComponentEvent e) {
	//	}
	//	public void componentShown(ComponentEvent e) {
	//	}
	//
	//	public void componentResized(ComponentEvent e) {
	//		Rectangle r = getBounds();
	//		r.x = 0;
	//		r.y = 0;
	//		Tool.getDrawPanel().setBounds(r);
	//	}

	public DrawLayers getLayeredPane() {
		return layeredPane;
	}

}