package jdraw.action;

import jdraw.data.Palette;
import jdraw.gui.Tool;

import java.awt.Color;
import java.awt.event.ActionEvent;

import util.Log;

/*
 * SetClipToolAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class AddColourAction extends DrawAction {

	protected AddColourAction() {
		super("Add Colour");
		setToolTipText("Adds a new colour to this palette");
	}

	public void actionPerformed(ActionEvent e) {
		Palette p = Tool.getCurrentPalette();
		p.addColour(Color.black);
		Log.info(
			"Colour #"
				+ String.valueOf(p.size())
				+ " created.");
	}

}
