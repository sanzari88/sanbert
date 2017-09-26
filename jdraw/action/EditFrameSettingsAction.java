package jdraw.action;

import jdraw.gui.DrawDialog;
import jdraw.gui.FrameSettingsDialog;
import jdraw.gui.MainFrame;
import jdraw.gui.SizeDialog;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * EditFrameSettingsAction - created on 03.11.2003
 * 
 * @author Michaela Behling
 */

public class EditFrameSettingsAction extends BlockingDrawAction {

	public EditFrameSettingsAction() {
		super("Settings...", "frame_settings.png");
		setToolTipText("Edits the current frame settings");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(
					new Character('E'),
					KeyEvent.CTRL_MASK)});
	}

	public boolean prepareAction() {
		FrameSettingsDialog dialog = new FrameSettingsDialog();
		dialog.open();
		if ( dialog.getResult() == FrameSettingsDialog.APPROVE ) {
			return true;
		}
		return false;
	}

	public void startAction() {
		
	}

	public void finishAction() {
	}

}
