package jdraw.action;

import jdraw.Main;
import jdraw.gui.DrawWindow;
import jdraw.gui.MainFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import util.Log;
import util.ResourceLoader;
import util.gui.AntialiasPanel;
import util.gui.GUIUtil;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class HelpAction extends DrawAction {

	private DrawWindow dialog;

	protected HelpAction() {
		super("Help...", "help.png");
		setToolTipText("Displays help about using " + Main.APP_NAME);
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('H'), KeyEvent.CTRL_MASK)});
	}

	public void actionPerformed(ActionEvent e) {
		if (dialog == null) {
			try {
				URL url = ResourceLoader.getResourceURL("jdraw/help/help.html");
				dialog = new DrawWindow("Help", false);
				JEditorPane editor = new JEditorPane(url) {

					public void paint(Graphics gr) {
						Graphics2D g2 = GUIUtil.createGraphics(gr);
						Object oldTextAntialias =
							g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);

						g2.setRenderingHint(
							RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

						super.paint(g2);

						g2.setRenderingHint(
							RenderingHints.KEY_TEXT_ANTIALIASING,
							oldTextAntialias);
					}
				};
				editor.addHyperlinkListener(new Hyperactive());
				editor.setEditable(false);
				JScrollPane pane = new JScrollPane(editor);
				Dimension dim = MainFrame.INSTANCE.getSize();
				dim.width = dim.width - (dim.width / 3);
				dim.height = dim.height - (dim.height / 3);
				pane.setPreferredSize(dim);

				dialog.setDefaultBorder();
				dialog.setUndecorated(false);
				dialog.setResizable(true);

				dialog.main.add(pane, BorderLayout.CENTER);

				dialog.addRightButton(dialog.getApproveButton());
				dialog.getApproveButton().setText("Close");
				dialog.getApproveButton().setMnemonic('c');
				dialog.addButtonPanel();
			}
			catch (IOException io) {
				Log.exception(io);
			}
		}
		else { // help window != null
			int state = dialog.getExtendedState();
			if ((state & DrawWindow.ICONIFIED) != 0) {
				state = state - DrawWindow.ICONIFIED;
				dialog.setExtendedState(state);
			}
		}
		if (dialog.isVisible()) {
			return;
		}
		dialog.open();
	}

	private class Hyperactive implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				try {
					pane.setPage(e.getURL());
				}
				catch (Throwable t) {
					Log.exception(t);
				}
			}
		}
	}

}
