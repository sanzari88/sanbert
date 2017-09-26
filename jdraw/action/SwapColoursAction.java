package jdraw.action;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.MainFrame;
import jdraw.gui.Tool;

import java.awt.event.ActionEvent;

import util.Log;

/*
 * SwapColoursAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SwapColoursAction extends DrawAction {

	protected SwapColoursAction() {
		super("Swap Colours");
		setToolTipText("Swaps foreground with background colour");
	}


	public void actionPerformed(ActionEvent e) {
		Picture pic = Tool.getPicture();
		Palette pal = Tool.getCurrentPalette();
		pal.swapColours(pic, pic.getForeground(), pic.getBackground());
		Log.info(
			"Swapped colours #"
				+ String.valueOf(pic.getForeground())
				+ " and #"
				+ String.valueOf(pic.getBackground()));
	}

}
