package jdraw.action;

import jdraw.gui.ClipTool;
import jdraw.gui.ColourPickerTool;
import jdraw.gui.FolderPanel;
import jdraw.gui.MainFrame;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import util.gui.GUIUtil;

/*
 * SetClipToolAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SetClipToolAction extends DrawAction {

	protected SetClipToolAction() {
		super("Clip Tool", "clip_tool.png");
		setToolTipText("Offers clip functionality");
	}

	public void actionPerformed(ActionEvent e) {
//		GUIUtil.info(
//			MainFrame.INSTANCE,
//			"Not implemented yet!",
//			"The Clip Tool isn't fully implemented yet. Check for available updates..."
//				+ "\n\nSorry about that!",
//			"Oops");
		ToolPanel.INSTANCE.setCurrentTool(new ClipTool());
	}
}
