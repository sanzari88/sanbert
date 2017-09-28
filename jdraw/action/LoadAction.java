package jdraw.action;

import jdraw.Main;
import jdraw.data.Clip;
import jdraw.data.Picture;
import jdraw.gio.GIFReader;
import jdraw.gio.IconReader;
import jdraw.gio.ImageReader;
import jdraw.gui.DrawBrowser;
import jdraw.gui.MainFrame;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;
import jdraw.gui.undo.UndoManager;
import salvataggio.SaveProgram;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.swing.KeyStroke;

import util.Log;
import util.Util;
import util.gui.GUIUtil;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class LoadAction extends BlockingDrawAction {

	private Picture picture;
	private String newFileName;

	protected LoadAction() {
		super("Apri programma", "open.png");
		setToolTipText("Apri programma esistente");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('O'), KeyEvent.CTRL_MASK)});
	}

	public static boolean isJDraw(String f) {
		int index = f.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = f.substring(index);
		return extension.equalsIgnoreCase(".jd");
	}

	public static boolean isICO(String f) {
		int index = f.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = f.substring(index);
		return extension.equalsIgnoreCase(".ico");
	}

	public static boolean isGIF(String f) {
		int index = f.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = f.substring(index);
		return extension.equalsIgnoreCase(".gif");
	}

	public static boolean isPNG(String f) {
		int index = f.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = f.substring(index);
		return extension.equalsIgnoreCase(".png");
	}

	public boolean prepareAction() {
		picture = null;
		File file = DrawBrowser.INSTANCE.openImage();
		if (file != null) {
			newFileName = file.getAbsolutePath();
			return true;
		}
		return false;
	}

	private Picture readJDraw(String fileName) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(fileName));
			SaveProgram programmaSalvato= (SaveProgram) in.readObject();;
			Clip.setMatriceMaglie(programmaSalvato.getC());
			return programmaSalvato.getP();
		}
		catch (Exception e) {
			Log.exception(e);
			return null;
		}
		finally {
			Util.close(in);
		}
	}

	public void startAction() {
//		if (isGIF(newFileName)) {
//			picture = GIFReader.readGIF(newFileName);
//			if ( picture == null ) {
//				boolean result = GUIUtil.question(MainFrame.INSTANCE,
//					"Retry?", "JDraw couldn't read this GIF image. Do you want "+
//					"to retry using Java's graphics library?", "Retry","Cancel");
//				if ( result ) {
//					picture = ImageReader.readImage(newFileName);
//				}
//			}
//		}
//		else 
			if (isJDraw(newFileName)) {
			picture = readJDraw(newFileName);
		}
//		else if (isICO(newFileName)) {
//			picture = IconReader.readIcon(newFileName);
//		}
		else {
			picture = ImageReader.readImage(newFileName);
		}
	}

	public void finishAction() {
		if (picture != null) {
			MainFrame.INSTANCE.setFileName(newFileName);
			Main.setPicture(picture);
			UndoManager.INSTANCE.reset();
			ToolPanel.INSTANCE.setCurrentTool(PixelTool.INSTANCE);
			Log.info("Opened.");
		}
	}
}
