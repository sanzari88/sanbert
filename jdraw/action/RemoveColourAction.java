package jdraw.action;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.MainFrame;
import jdraw.gui.Tool;

import java.awt.event.ActionEvent;

import util.Log;

/*
 * RemoveColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class RemoveColourAction extends BlockingDrawAction {

	protected RemoveColourAction() {
		super("Remove Colour");
		setToolTipText("Removes the current foreground colour from this palette");
	}


	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Picture pic = Tool.getPicture();
		Palette pal = Tool.getCurrentPalette();
		final int index = pic.getForeground();
		pal.removeColour(index);
		Log.info("Colour #" + String.valueOf(index) + " removed.");
	}

	public void finishAction() {
	}
}
