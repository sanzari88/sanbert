package jdraw.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jdraw.Main;
import jdraw.data.Picture;
import jdraw.gui.DrawDialog;
import jdraw.gui.MainFrame;
import jdraw.gui.SizeDialog;
import jdraw.gui.undo.UndoManager;
import util.Log;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class NewAction extends DrawAction {

	protected NewAction() {
		super("Nuovo programma", "new.png");
		setToolTipText("Crea un nuovo programma");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('N'), KeyEvent.CTRL_MASK)});
	}

	public void actionPerformed(ActionEvent e) {
		MainFrame.INSTANCE.setFileName( null );
		
		UndoManager.INSTANCE.reset();
		Dimension dimension;
		SizeDialog d = new SizeDialog();
		d.open();
		if (d.getResult() == DrawDialog.APPROVE) {
			dimension = d.getInput();
			Main.setPicture( Picture.createNewPicture(dimension.width, dimension.height) );
		}
		
		
		Log.info( "Nuovo programma creato con successo.");
	}


}
