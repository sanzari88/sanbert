package jdraw.action;

import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.KeyStroke;

import jdraw.data.Clip;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gio.GIFWriter;
import jdraw.gio.IconWriter;
import jdraw.gio.PNGWriter;
import jdraw.gui.MainFrame;
import jdraw.gui.Tool;
import jdraw.gui.undo.UndoManager;
import salvataggio.SaveProgram;
import util.Log;
import util.Util;

/*
 * SaveAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class SaveAction extends BlockingDrawAction {

	private String fileName;
	private boolean success;

	protected SaveAction() {
		super("Salva", "save.png");
		setToolTipText("Salva programma corrente");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('S'), KeyEvent.CTRL_MASK)});
	}

	public boolean prepareAction() {
		fileName = MainFrame.INSTANCE.getFileName();
		if (fileName == null) {
			DrawAction.getAction(SaveAsAction.class).actionPerformed();
			return false;
		}
		return true;
	}

	private boolean saveJDraw(String aFileName) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(aFileName));
			SaveProgram save = new SaveProgram(Tool.getPicture(), Clip.getMatriceMaglia(),Clip.getMatriceComandi());
			//out.writeObject(Tool.getPicture());
			out.writeObject(save);
			return true;
		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
		finally {
			Util.close(out);
		}
	}

	private boolean saveInterlaced() {
		SaveAsAction action = (SaveAsAction) getAction(SaveAsAction.class);
		return action.saveInterlaced();
	}

	public void startAction() {

//		if (LoadAction.isGIF(fileName)) {
//			success =
//				ViewAnimationAction.checkGIFColours()
//					&& GIFWriter.writeGIF(
//						MainFrame.INSTANCE.getPicture(),
//						fileName,
//						saveInterlaced());
//		}
//		else 
		if (LoadAction.isAtrs(fileName)) {
			success = saveJDraw(fileName);
		}
//		else if (LoadAction.isICO(fileName)) {
//			success =
//				ViewAnimationAction.checkIconColours()
//					&& IconWriter.writeIcon(MainFrame.INSTANCE.getPicture(), fileName);
//		}
		else if (LoadAction.isPNG(fileName)) {
			success =
				PNGWriter.writePNG(
					MainFrame.INSTANCE.getPicture(),
					fileName,
					saveInterlaced());
		}
		else {
			success = false;
			Log.warning("Formato immagine non supportato.");
		}
	}

	public void finishAction() {
		if (success) {
			Log.info("Programma salvato in " + fileName);
			UndoManager.INSTANCE.reset();
		}
	}

}
