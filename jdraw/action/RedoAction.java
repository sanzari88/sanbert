package jdraw.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jdraw.gui.undo.UndoManager;

/*
 * UndoAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class RedoAction extends DrawAction {

	protected RedoAction() {
		super("Ripeti", "redo.png");
		setToolTipText("Ripeti azione");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(
					new Character('Z'),
					KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK)});
	}

	public void actionPerformed(ActionEvent e) {
		UndoManager.INSTANCE.redo();		
	}

}
