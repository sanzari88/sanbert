package jdraw.action;

import jdraw.gui.OvalTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;

/*
 * SetRectangleToolAction.java - created on 15.11.2003
 * 
 * @author Michaela Behling
 */

public class SetFilledOvalToolAction extends DrawAction {

	protected SetFilledOvalToolAction() {
		super("Filled Oval Tool", "filled_oval_tool.png");
		setToolTipText("Disegna ovale pieno");
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new OvalTool(true));
	}

}
