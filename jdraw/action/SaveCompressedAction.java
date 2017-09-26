package jdraw.action;

import jdraw.data.Picture;
import jdraw.gio.GIFWriter;
import jdraw.gui.MainFrame;
import jdraw.gui.undo.UndoManager;
import util.Log;

/*
 * SaveAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class SaveCompressedAction extends BlockingDrawAction {

	private String fileName;
	private boolean success;

	protected SaveCompressedAction() {
		super("Save Compressed");
		setToolTipText("Compresses and saves the current image");		
	}

	public boolean prepareAction() {
		fileName = MainFrame.INSTANCE.getFileName();
		if (fileName == null) {
			((SaveAsAction)DrawAction.getAction(SaveAsAction.class)).actionPerformed(this);
			return false;
		}
		return true;

	}

	public void startAction() {
		Picture picture = MainFrame.INSTANCE.getPicture();
		picture.compress();
		success = GIFWriter.writeGIF( picture, fileName);
	}

	public void finishAction() {
		if (success) {
			Log.info("Saved compressed to " + fileName);
			UndoManager.INSTANCE.reset();
		}
	}

}
