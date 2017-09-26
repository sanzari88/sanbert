package jdraw.action;

import jdraw.gui.ClipTool;
import jdraw.gui.ColourPickerTool;
import jdraw.gui.FolderPanel;
import jdraw.gui.PixelTool;
import jdraw.gui.RectangleTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * SetRectangleToolAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SetRectangleToolAction extends DrawAction {

	protected SetRectangleToolAction() {
		super("Rectangle Tool", "rectangle_tool.png");
		setToolTipText("Disegna rettangolo");
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new RectangleTool());
	}

}
