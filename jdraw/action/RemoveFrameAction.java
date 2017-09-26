package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.Tool;

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

public class RemoveFrameAction extends DrawAction {

	protected RemoveFrameAction() {
		super("Remove Frame", "delete_frame.png");
		setToolTipText("Removes the current frame");
		
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		Tool.getPicture().removeCurrentFrame();
		setEnabled(Tool.getPicture().getFrameCount() > 1);
		if (Log.DEBUG)
		Log.info("Frame removed.");
	}

}
