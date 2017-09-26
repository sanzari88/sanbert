package jdraw.action;

import jdraw.gui.OvalTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;

/*
 * SetOvalToolAction.java - created on 15.11.2003
 * 
 * @author Michaela Behling
 */

public class SetOvalToolAction extends DrawAction {

	protected SetOvalToolAction() {
		super("Oval Tool", "oval_tool.png");
		setToolTipText("Disegna ovale");
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new OvalTool(false));
	}

}
