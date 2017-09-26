package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.Tool;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * AddFrameAction.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class AddFrameAction extends DrawAction {

	protected AddFrameAction() {
		super("Add Frame", "frame_new.png");
		setToolTipText("Adds a new frame");		
	}

	public void actionPerformed(ActionEvent e) {
		Tool.getPicture().addFrame();
		DrawAction.getAction(RemoveFrameAction.class).setEnabled(true);
		Log.info("Frame added");
	}

}
