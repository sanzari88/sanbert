package jdraw.action;

import jdraw.gui.DrawPanel;
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

public class SetPreviousZoomAction extends DrawAction implements GridListener {

	private int zoom = -1;

	protected SetPreviousZoomAction() {
		super("Set Previous Zoom");
		setToolTipText("Sets previous zoom level");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke(new Character('8'), 0)});
		setEnabled(false);
		FolderPanel.addGridListener(this);
	}

	private void setPreviousZoom(int level) {
		zoom = level;
		setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (isEnabled()) {
			FolderPanel.setGrid(zoom);
			setEnabled(false);
			Log.info("Previous zoom level set.");
		}
	}

	public void gridChanged(int oldValue, int newValue) {
		setPreviousZoom(oldValue);
	}

}
