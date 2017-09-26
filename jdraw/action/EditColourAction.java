package jdraw.action;

import java.awt.Color;

import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.gui.ColourEditor;
import jdraw.gui.Tool;

/*
 * EditColourAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class EditColourAction extends BlockingDrawAction {

	private ColourEditor editor;
	private int colIndex;
	private Color newColour;

	protected EditColourAction() {
		super("Edit Colour...");
		setToolTipText("Edits the current foreground colour");
	}

	public boolean prepareAction() {
		editor = ColourEditor.INSTANCE;
		Frame frame = Tool.getCurrentFrame();
		Palette p = frame.getPalette();
		colIndex = Tool.getPicture().getForeground();

		editor.setColour(p.getColour(colIndex).getColour());
		editor.open();
		return editor.getResult() == ColourEditor.APPROVE;
	}

	public void startAction() {
		Tool.getCurrentFrame().getPalette().setColour(
			colIndex,
			editor.getColour());
	}

	public void finishAction() {
	}
}
