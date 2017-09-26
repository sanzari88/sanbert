package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.FramePanel;
import jdraw.gui.Tool;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class ToggleTransparencyAction extends DrawAction {

	protected ToggleTransparencyAction() {
		super("Transparency as Colour");
		setToolTipText("Displays transparent pixels either as pattern or as colour");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke(new Character(' '), 0)});
	}

	public void actionPerformed(ActionEvent e) {
		FramePanel p = Tool.getCurrentFramePanel();
		p.toggleTransparencyMode();
		Log.info((String) getValue(NAME));
		if (p.getTransparencyMode() == FramePanel.SHOW_PATTERN) {
			putValue(NAME, "Transparency as Colour");
		}
		else {
			putValue(NAME, "Transparency as Pattern");
		}
	}

}
