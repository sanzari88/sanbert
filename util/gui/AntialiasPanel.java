package util.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/*
 * AntialiasPanel.java - created on 17.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class AntialiasPanel extends JPanel {

	private boolean antialiasText;
	private boolean antialiasGraphics;

	public AntialiasPanel() {
		this(true, true);
	}

	public AntialiasPanel(
		boolean antialiasTextFlag,
		boolean antialiasGraphicsFlag) {
		antialiasText = antialiasTextFlag;
		antialiasGraphics = antialiasGraphicsFlag;
	}

	public AntialiasPanel(LayoutManager manager) {
		this(manager, true, true);
	}

	public AntialiasPanel(
		LayoutManager manager,
		boolean antialiasTextFlag,
		boolean antialiasGraphicsFlag) {
		super(manager);
		antialiasText = antialiasTextFlag;
		antialiasGraphics = antialiasGraphicsFlag;
	}

	public final void paint(Graphics gr) {
		Graphics2D g2 = GUIUtil.createGraphics(gr);
		Object oldTextAntialias =
			g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		Object oldGraphicsAntialias =
			g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

		if (antialiasText) {
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		if (antialiasGraphics) {
			g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}

		super.paint(g2);

		if (antialiasText) {
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				oldTextAntialias);
		}
		if (antialiasGraphics) {
			g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				oldGraphicsAntialias);
		}
	}

}
