package jdraw.gui;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.swing.ImageIcon;

import jdraw.data.Picture;
import jdraw.gio.GIFWriter;
import jdraw.gio.IconReader;
import salvataggio.SaveProgram;
import util.Log;
import util.Util;
import util.gui.IconViewer;

/*
 * ICOHandler.java - created on 09.12.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class JDHandler implements IconViewer.ImageHandler {

	public JDHandler() {
	}

	public final boolean canHandleImage(String fileName) {
		return Util.getFileExtension(fileName).equalsIgnoreCase(".jd");
	}

	public ImageIcon createIconLabel(String fileName) {
		ObjectInputStream in = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			in = new ObjectInputStream(new FileInputStream(fileName));
			// Separo l'immagine dalla programmazione
			SaveProgram programmaSalvato =(SaveProgram)in.readObject();
			Picture picture = programmaSalvato.getP();
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
			Util.close(in);
		}
		return null;
	}

}
