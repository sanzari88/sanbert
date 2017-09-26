package jdraw.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.KeyStroke;

import jdraw.gui.DrawBrowser;
import jdraw.gui.MainFrame;
import util.gui.GUIUtil;

/*
 * SaveAsAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class SaveAsAction extends DrawAction {

	private boolean saveInterlaced = false;

	protected SaveAsAction() {
		super("Salva in..", "save_as.png");
		setToolTipText("Salva programma corrente in ...");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('A'), KeyEvent.CTRL_MASK)});
	}

	public void actionPerformed(DrawAction saveAction) {
		saveInterlaced = false;
		File file = DrawBrowser.INSTANCE.saveImage();
		if (file != null) {
			if (file.exists()) {
				boolean overwrite =
					GUIUtil.question(
						MainFrame.INSTANCE,
						"Replace existing file?",
						"The file <font color=blue>"
							+ file.getName()
							+ "</font> already exists. Do you want to replace it?",
						"Replace",
						"Cancel");
				if (!overwrite) {
					return;
				}
			}
			saveInterlaced = DrawBrowser.INSTANCE.saveInterlaced();
			MainFrame.INSTANCE.setFileName(file.getAbsolutePath());
			saveAction.actionPerformed();
		}
	}

	public boolean saveInterlaced() {
		return saveInterlaced;
	}

	public void actionPerformed(ActionEvent e) {
		actionPerformed(DrawAction.getAction(SaveAction.class));
	}

}
