package jdraw.action;

import jdraw.gui.DrawDialog;
import jdraw.gui.MainFrame;
import jdraw.gui.SizeDialog;
import jdraw.gui.undo.UndoManager;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * ResizeAction.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class ResizeAction extends BlockingDrawAction {

	private Dimension dimension;

	public ResizeAction() {
		super("Ridimensiona", "resize.png");
		setToolTipText("Ridimensiona disegno");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(
					new Character('R'),
					KeyEvent.CTRL_MASK)});
	}

	public boolean prepareAction() {
		SizeDialog d = new SizeDialog();
		d.open();
		if (d.getResult() == DrawDialog.APPROVE) {
			dimension = d.getInput();
			return true;
		}
		return false;
	}

	public void startAction() {
		MainFrame.INSTANCE.getPicture().setSize(
			dimension.width,
			dimension.height);
	}

	public void finishAction() {
		UndoManager.INSTANCE.reset();
	}

}
