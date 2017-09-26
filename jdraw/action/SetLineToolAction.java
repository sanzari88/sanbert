package jdraw.action;

import jdraw.gui.ClipTool;
import jdraw.gui.ColourPickerTool;
import jdraw.gui.FolderPanel;
import jdraw.gui.LineTool;
import jdraw.gui.PixelTool;
import jdraw.gui.RectangleTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * SetLineToolAction.java - created on 15.11.2003
 * 
 * @author Michaela Behling
 */

public class SetLineToolAction extends DrawAction {

	protected SetLineToolAction() {
		super("Line Tool", "line_tool.png");
		setToolTipText("Disegna linea");
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new LineTool());
	}

}
