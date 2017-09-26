package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.MainFrame;

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

public class ToggleViewsAction extends DrawAction {

	private boolean show = true;

	protected ToggleViewsAction() {
		super("Nascondi strumenti disegno");
		setToolTipText("Mostra o nascondi strumenti da disegno");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke(new Character('H'), 0)});
	}

	public void actionPerformed(ActionEvent e) {
		show = !show;
		if (show) {
			putValue(NAME, "Nascondi strumenti disegno");
			Log.info("Views showing.");
		}
		else {
			putValue(NAME, "Mostra strumenti disegno");
			Log.info("Views hidden.");
		}
		MainFrame.INSTANCE.showViews(show);
	}

}
