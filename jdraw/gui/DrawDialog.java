package jdraw.gui;

import javax.swing.JFrame;

import util.gui.StandardDialog;

/*
 * DrawDialog.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class DrawDialog extends StandardDialog {

	public DrawDialog(JFrame parent, String title, boolean ignoreESCKey) {
		super(parent, title, ignoreESCKey);
	}

	public DrawDialog(String title, boolean ignoreESCKey) {
		super(MainFrame.INSTANCE, title, ignoreESCKey);		
	}

	public DrawDialog(String title) {
		this(title, false);
	}

}