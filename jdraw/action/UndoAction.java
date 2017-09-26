package jdraw.action;

import jdraw.gui.FolderPanel;
import jdraw.gui.undo.UndoManager;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * UndoAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class UndoAction extends DrawAction {

	protected UndoAction() {
		super("Annulla", "undo.png");
		setToolTipText("Azione annullata");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(
					new Character('Z'),
					KeyEvent.CTRL_MASK)});
	}

	public void actionPerformed(ActionEvent e) {
		UndoManager.INSTANCE.undo();		
	}

}
