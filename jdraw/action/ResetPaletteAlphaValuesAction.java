package jdraw.action;

import jdraw.gui.Tool;

/*
 * RemoveColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class ResetPaletteAlphaValuesAction extends BlockingDrawAction {

	private int result = 0;

	protected ResetPaletteAlphaValuesAction() {
		super( "Reset Alpha Values" );
		setToolTipText("Removes the alpha channel in this palette" );		
	}


	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Tool.getCurrentPalette().removeAlphaValues();
	}

	public void finishAction() {		
	}
}
