package jdraw.action;

import jdraw.data.Palette;
import jdraw.gui.Tool;

/*
 * RemoveColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class ReducePaletteColoursAction extends BlockingDrawAction {

	private int result = 0;

	protected ReducePaletteColoursAction() {
		super(
			"Reduce palette to " + String.valueOf(Palette.GIF_MAX_COLOURS) + " colours");
		setToolTipText("Reduces each frame using this palette to maximal 255 colours");
	}


	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Palette pal =Tool.getCurrentPalette();
		result = pal.reduceColours();
	}

	public void finishAction() {
		CompressAction.showResult(result);
	}
}
