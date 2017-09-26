package jdraw.gui;

import java.io.ByteArrayOutputStream;

import javax.swing.ImageIcon;

import jdraw.data.Picture;
import jdraw.gio.GIFWriter;
import jdraw.gio.IconReader;

import util.Log;
import util.Util;
import util.gui.IconViewer;

/*
 * ICOHandler.java - created on 09.12.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class ICOHandler implements IconViewer.ImageHandler {

	public ICOHandler() {
	}

	public final boolean canHandleImage(String fileName) {
		return Util.getFileExtension(fileName).equalsIgnoreCase(".ico");
	}

	public ImageIcon createIconLabel(String fileName) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			Picture picture = IconReader.readIcon(fileName);
			if (picture != null) {				
				GIFWriter.writeGIF(picture, stream);
				return new ImageIcon(stream.toByteArray());						
			}
		}
		catch (Exception e) {
			Log.exception(e);
		}
		finally {
			Util.close(stream);
		}
		return null;
	}

}
