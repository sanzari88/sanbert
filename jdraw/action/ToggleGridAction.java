package jdraw.action;

import jdraw.gui.FolderPanel;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * Action.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class ToggleGridAction extends DrawAction {

	protected ToggleGridAction() {
		super("Nascondi Griglia");
		setToolTipText("Mostra o nascondi griglia immagine");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(
					new Character('G'),
					KeyEvent.CTRL_MASK)});
	}

	public void actionPerformed(ActionEvent e) {
		FolderPanel p = FolderPanel.INSTANCE;
		p.setShowGrid(!p.showGrid());
		if (p.showGrid()) {
			putValue(NAME, "Nascondi Griglia");
			Log.info("Grid showing.");
		}
		else {
			putValue(NAME, "Mostra Griglia");
			Log.info("Grid hidden.");
		}
	}

}


