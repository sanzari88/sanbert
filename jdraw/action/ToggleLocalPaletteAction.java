package jdraw.action;

import jdraw.data.Frame;
import jdraw.data.Picture;
import jdraw.gui.Tool;

import java.awt.Color;
import java.awt.event.ActionEvent;

import util.Log;

/*
 * ToggleLocalPaletteAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class ToggleLocalPaletteAction extends DrawAction {

	protected ToggleLocalPaletteAction() {
		super("Create Local Palette");
		setToolTipText("Creates a local palette for this frame");
	}

	public void actionPerformed(ActionEvent e) {
		Picture pic = Tool.getPicture();
		Frame frame = pic.getCurrentFrame();

		frame.toggleLocalPalette();
		if (frame.getPalette().isGlobalPalette()) {
			putValue(NAME, "Create Local Palette");
			Log.info("Using global palette.");
		}
		else {
			putValue(NAME, "Discard Local Palette");
			Log.info("Using local palette.");
		}
	}

}
