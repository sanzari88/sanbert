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

public class DecreaseZoomAction extends DrawAction implements GridListener {

	protected DecreaseZoomAction() {
		super("Riduci Zoom", "zoom_out.png");
		setAccelerators(new KeyStroke[] { KeyStroke.getKeyStroke('-')});
		setToolTipText("Zooms out ");
		FolderPanel.addGridListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (isEnabled()) {
			if (FolderPanel.getGrid() > FolderPanel.MIN_GRID) {
				FolderPanel.setGrid(FolderPanel.getGrid() - 1);
				Log.info("Zoom decrementato.");
			}
		}
	}

	public void gridChanged(int oldValue, int newValue) {
		setEnabled(newValue > FolderPanel.MIN_GRID);
	}

}
