package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * SetPixelToolAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SetPixelToolAction extends DrawAction {

	protected SetPixelToolAction() {
		super("Pixel Tool", "pixel_tool.png");
		setToolTipText("Matita");		
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(PixelTool.INSTANCE);		
	}

}
