package jdraw.action;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.Tool;

/*
 * RemoveColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class ReduceColoursAction extends BlockingDrawAction {

	private int result = 0;

	protected ReduceColoursAction() {
		super(
			"Reduce picture to " + String.valueOf(Palette.GIF_MAX_COLOURS) + " colours");
		setToolTipText("Reduces each frame's palette to maximal 255 colours");
	}


	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		Picture pic = Tool.getPicture();
		result = pic.reduceColours();
	}

	public void finishAction() {
		CompressAction.showResult(result);
	}
}
