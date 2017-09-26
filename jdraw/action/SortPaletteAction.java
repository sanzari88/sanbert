package jdraw.action;

import java.awt.Color;

import jdraw.gui.ColourEditor;
import jdraw.gui.Tool;
import util.Log;

/*
 * EditColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class SortPaletteAction extends BlockingDrawAction {

	private ColourEditor editor;
	private int colIndex;
	private Color newColour;

	protected SortPaletteAction() {
		super("Sort Colours");
		setToolTipText("Sort the colours in this palette");
	}

	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Tool.getCurrentFrame().getPalette().sort();
	}

	public void finishAction() {
		Log.info("Sorted.");
	}
}
