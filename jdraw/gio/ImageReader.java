package jdraw.gio;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import jdraw.data.ColourEntry;
import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Assert;
import util.Log;
import util.SimpleLogListener;
import util.Util;
import util.gui.GUIUtil;

/*
 * JImageReader.java - created on 25.11.2003
 * 
 * @author Michaela Behling
 */

public final class ImageReader {

	private final ImageIcon icon;
	private Picture picture;
	private Palette pal;
	private Frame frame;
	private int width;
	private int height;
	private int[] grabbedPixels;
	private final ArrayList transColours = new ArrayList();

	private ImageReader(ImageIcon anIcon) {
		icon = anIcon;
	}

	public static Picture readImage(String aFileName) {
		return readImage(new ImageIcon(aFileName));
	}

	public static Picture readImage(ImageIcon anIcon) {
		ImageReader reader = new ImageReader(anIcon);
		try {
			Picture pic = reader.readImage();
			Palette palette = pic.getPalette();
			while (palette.size() < 2) {
				palette.addColour(Color.white);
			}
			pic.show();
			return pic;
		}
		catch (Exception e) {
			Log.exception(e);
			reader.picture = null;
			reader.pal = null;
			return null;
		}
	}

	private Picture readImage() throws Exception {
		Image img = icon.getImage();
		width = icon.getIconWidth();
		height = icon.getIconHeight();
		grabbedPixels = new int[width * height];
		PixelGrabber grabber =
			new PixelGrabber(img, 0, 0, width, height, grabbedPixels, 0, width);
		try {
			if (!grabber.grabPixels()) {
				if (Log.DEBUG)
					Log.debug("gio: grabbing pixels failed");
				return null;
			}
		}
		catch (InterruptedException e) {
			Log.exception(e);
			return null;
		}
		return createPicture(grabber);
	}

	private Picture createPicture(PixelGrabber grabber) {
		picture = new Picture(width, height);
		pal = new Palette(picture);
		picture.setPalette(pal);
		frame = picture.addFrame();

		int i = 0;
		int col;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				col = indexOfPixelColour(grabbedPixels[i]);
				frame.setPixelQuiet(x, y, col);
				i++;
			}
		}
		setTransparency();
		return picture;
	}

	private final int indexOfPixelColour(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;

		Color c = new Color(red, green, blue, alpha);
		int index = pal.findColour(c);
		if (index == -1) {
			index = pal.size();
			pal.addColour(c);
			if (alpha == ColourEntry.TRANSPARENT) {
				transColours.add(c);
			}
		}
		ColourEntry e = pal.getColour(index);
		e.increaseUsed();
		return index;
	}

	private void setTransparency() {
		ColourEntry entry = null;
		ColourEntry e;
		Iterator it = transColours.iterator();
		Color col;
		while (it.hasNext()) {
			col = (Color) it.next();
			e = pal.findMaxUsedColorEntryForColour(col);
			Assert.notNull(e, "colour not found");
			if ((entry == null) || (entry.getUsedCount() < e.getUsedCount()))
				entry = e;
		}
		if (entry != null) {
			// verwendete trans farben auf die h�ufigste umindizieren			
			it = transColours.iterator();
			while (it.hasNext()) {
				col = (Color) it.next();
				e = pal.findMaxUsedColorEntryForColour(col);
				if (e != entry) {
					pal.replaceColour(e.getIndex(), entry.getIndex());
					e.reset();
				}
			}
			pal.purgeColours();
			picture.setTransparent(entry.getIndex());
		}
	}

	public static void main(String[] args) {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Log.addLogListener(listener);
		readImage("C:/Java/Projects/JDraw/penguin.png");
	}

}
