package jdraw.action;

import jdraw.Main;
import jdraw.gui.AboutDialog;

import java.awt.event.ActionEvent;

/*
 * AboutAction - created on 16.11.2003
 * 
 * @author Michaela Behling
 */

public class AboutAction extends DrawAction {

	public AboutAction() {
		super("About...", "about.png");
		setToolTipText("Displays information about "+Main.APP_NAME);
	}

	public void actionPerformed(ActionEvent e) {
		AboutDialog dialog = new AboutDialog();
		dialog.open();
	}

}
