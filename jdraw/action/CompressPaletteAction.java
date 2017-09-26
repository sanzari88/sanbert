package jdraw.action;

import jdraw.gui.Tool;

/*
 * SaveAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class CompressPaletteAction extends BlockingDrawAction {

	private int result;

	private String fileName;
	private boolean success;

	protected CompressPaletteAction() {
		super("Remove unused colours");
		setToolTipText("Removes all unused colours");
	}

	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		result = Tool.getCurrentPalette().compress();
	}

	public void finishAction() {
		CompressAction.showResult(result);
	}
}
