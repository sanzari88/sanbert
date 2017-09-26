package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.FramePanel;
import jdraw.gui.Tool;
import jdraw.gui.ToolPanel;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * ToggleGradientAction.java - created on 26.11.2003
 * 
 * @author Michaela Behling
 */

public class ToggleGradientFillAction extends DrawAction {

	private boolean gradientFill = false;

	protected ToggleGradientFillAction() {
		super("Gradient Fill");
		setToolTipText("Turns gradient fill on/off");
		setEnabled(false);
	}

	protected void setGradientFill(boolean flag) {
		gradientFill = flag;
		ToolPanel.INSTANCE.selectGradientFill(flag);
	}

	public boolean gradientFillOn() {
		return isEnabled() && gradientFill;
	}

	public void actionPerformed(ActionEvent e) {
		gradientFill = !gradientFill;
	}

}
