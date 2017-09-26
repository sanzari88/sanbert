package jdraw.action;

import jdraw.gui.FillTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;

/*
 * SetFillToolAction.java - created on 15.11.2003
 * 
 * @author Michaela Behling
 */

public class SetFillToolAction extends DrawAction {

	protected SetFillToolAction() {
		super("Fill Tool", "fill_tool.png");
		setToolTipText("Riempi zona");
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new FillTool());
	}

}
