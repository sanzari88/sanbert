package jdraw.action;

import java.awt.event.ActionEvent;

import jdraw.gui.ToolPanel;

/*
 * ToggleAntialiasAction.java - created on 16.11.2003
 * 
 * @author Michaela Behling
 */

public class ToggleAntialiasAction extends DrawAction {

	private boolean antialiasOn = true;

	protected ToggleAntialiasAction() {
		super("Antialias");
		setToolTipText("Turns antialias on/off");
		setEnabled(false);
	}

	protected void setAntialias(boolean flag) {
		antialiasOn = flag;
		ToolPanel.INSTANCE.selectAntialias(flag);
	}

	public boolean antialiasOn() {
		return isEnabled() && antialiasOn;
	}

	public void actionPerformed(ActionEvent e) {
		antialiasOn = !antialiasOn;
	}

}
