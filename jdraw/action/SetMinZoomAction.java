package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.GridListener;

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

public class SetMinZoomAction extends DrawAction implements GridListener {

	protected SetMinZoomAction() {
		super("Min Zoom");
		setToolTipText("Minimo livello di zoom");
		setAccelerators(new KeyStroke[] { KeyStroke.getKeyStroke('0')});
		FolderPanel.addGridListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		FolderPanel.setGrid(FolderPanel.MIN_GRID);
		Log.info("Minimo livello di zoom");
	}

	public void gridChanged(int oldValue, int newValue) {
		setEnabled(newValue>FolderPanel.MIN_GRID);
	}

}
