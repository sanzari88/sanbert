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

public class IncreaseZoomAction extends DrawAction implements GridListener {

	protected IncreaseZoomAction() {
		super("Aumenta Zoom", "zoom_in.png");
		setToolTipText("Zooms in ");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke(new Character('+'), 0)});
		FolderPanel.addGridListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (isEnabled()) {
			if (FolderPanel.getGrid() < FolderPanel.MAX_GRID) {
				FolderPanel.setGrid(FolderPanel.getGrid() + 1);
				Log.info("Zoom aumentato.");
			}
		}
	}

	public void gridChanged(int oldValue, int newValue) {
		setEnabled(newValue < FolderPanel.MAX_GRID);
	}

}
