package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.GridListener;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import util.Log;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SetMaxZoomAction extends DrawAction implements GridListener {

	protected SetMaxZoomAction() {
		super("Max Zoom");
		setToolTipText("Massimo livello di zoom");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke('9')});
		FolderPanel.addGridListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		FolderPanel.setGrid(FolderPanel.MAX_GRID);
		Log.info("Massimo livello di zoom");		
	}

	public void gridChanged(int oldValue, int newValue) {
		setEnabled(newValue != FolderPanel.MAX_GRID);
	}

}
