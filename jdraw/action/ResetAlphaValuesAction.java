package jdraw.action;

import jdraw.gui.Tool;

/*
 * RemoveColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class ResetAlphaValuesAction extends BlockingDrawAction {

	private int result = 0;

	protected ResetAlphaValuesAction() {
		super( "Reset Alpha Values" );
		setToolTipText("Removes the alpha channel in this picture" );		
	}


	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Tool.getPicture().removeAlphaChannel();
		
	}

	public void finishAction() {

	}
}
