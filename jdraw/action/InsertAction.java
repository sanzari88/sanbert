package jdraw.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gio.GIFReader;
import jdraw.gio.ImageReader;
import jdraw.gui.DrawBrowser;
import jdraw.gui.MainFrame;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;
import jdraw.gui.undo.UndoManager;
import util.Log;
import util.Util;

/*
 * InsertAction.java - created on 29.11.2003
 * 
 * @author Michaela Behling
 */

public final class InsertAction extends BlockingDrawAction {

	private String fileName;
	private Picture picture;

	protected InsertAction() {
		super("Insert Image...");
		setToolTipText("Inserts an image into the current frame");
	}

	public static boolean isJDraw(String f) {
		int index = f.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = f.substring(index);
		return extension.equalsIgnoreCase(".jd");
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
			fileName = file.getAbsolutePath();
			return true;
		}
		return false;
	}

	private Picture readJDraw(String aFileName) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(aFileName));
			return (Picture) in.readObject();
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
		if (isGIF(fileName)) {
			picture = GIFReader.readGIF(fileName);
		}
		else if (isJDraw(fileName)) {
			picture = readJDraw(fileName);
		}
		else {
			picture = ImageReader.readImage(fileName);
		}
	}

	public void finishAction() {
		if (picture != null) {
			int w = picture.getWidth();
			int h = picture.getHeight();
			Picture pic = MainFrame.INSTANCE.getPicture();
			w = Math.max(pic.getWidth(), w);
			h = Math.max(pic.getHeight(), h);
			if ((w != pic.getWidth()) || (h != pic.getHeight())) {
				pic.setSize(w, h);
			}
			Palette pal = pic.getCurrentPalette();
			Frame frame = picture.getFrame(0);
			frame.addIndex(pal.size());
			Palette palette = frame.getPalette();
			final int size = palette.size();
			for (int i = 0; i < size; i++) {
				pal.addColour(palette.getColour(i).getColour());
			}
			pic.getCurrentFrame().pasteClip(frame, 0, 0);
			UndoManager.INSTANCE.reset();
			ToolPanel.INSTANCE.setCurrentTool(PixelTool.INSTANCE);
			Log.info("Inserted.");
		}
	}
}
