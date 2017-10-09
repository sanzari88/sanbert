package jdraw.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import jdraw.Main;
import jdraw.data.event.ChangeEvent;
import jdraw.gio.ImageReader;
import jdraw.gio.PNGWriter;
import jdraw.gui.Tool;
import magliera.puntoMaglia.TipoLavoroEnum;
import util.Log;
import util.Util;
import util.gui.GUIUtil;

/*
 * Picture.java - created on 27.10.2003
 * 
 * @author Michaela Behling
 */

public final class Picture extends DataObject {

	private static long serialVersionUID = 0L;

	public static Rectangle geometry = null;
	private int background = 0;
	private int currentFrame = -1;
	private int foreground = 1;

	private final ArrayList frames = new ArrayList();

	private Palette palette;
	private int pictureBackground = 0;
	private int transparent = -1;

	private int width;
	private int height;

	public Picture(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight;
	}

	public static Picture createDefaultPicture() {
		Picture pic = createPicture(10, 10);
		
		
		pic.setPalette(Palette.createDefaultPalette(pic));
		// pic.setTransparent(0);
		pic.setBackground(0);
		
		// setto come default all'apertura del programma il rasato anteriore
		jdraw.gui.ToolPanel.setMagliaSelezionata(TipoLavoroEnum.MagliaAnteriore.toString());
		//pic.setForeground(64);  //tolgo il colore impostato di default
		Clip.makeMagliaAndComandi(10, 10);

		return pic;
	}
	
	public static Picture createPictureComandi() {
		Picture picComandi= createPictureComandi(10, 5);
		picComandi.setBackground(0);
		return picComandi;
	}
	
	
	public static Picture createNewPicture(int x, int y) {
		Picture pic = createPicture(x, y);
		pic.setPalette(Palette.createDefaultPalette(pic));
		// pic.setTransparent(0);
		pic.setBackground(0);
		//pic.setForeground(64);  //tolgo il colore impostato di default
		// setto come default all'apertura del programma il rasato anteriore
		jdraw.gui.ToolPanel.setMagliaSelezionata(TipoLavoroEnum.MagliaAnteriore.toString());
		Clip.makeMagliaAndComandi(y, x);

		return pic;
	}

	public void scale(Dimension dimension, int scaleStyle) {
		final int frameCount = getFrameCount();
		Frame f;
		ByteArrayOutputStream out = null;
		ImageIcon icon;

		Picture scaledPic;
		ArrayList newFrameList = new ArrayList(frameCount);
		for (int i = 0; i < frameCount; i++) {
			try {
				f = getFrame(i).copy(this);				
				out = new ByteArrayOutputStream();

				if (PNGWriter.writePNG(f, out)) {
					icon = new ImageIcon(out.toByteArray());
					GUIUtil.waitForImage(icon);
					icon = util.ResourceLoader.scaleImage(icon, dimension, scaleStyle);
					scaledPic = ImageReader.readImage(icon);
					f = scaledPic.getFrame(0);
					f.setPalette(scaledPic.getPalette());					
					f.setTransparent(scaledPic.getTransparent());					
					newFrameList.add(f);
				}
				else {
					Log.error("Scaling failed. Internal error.");
				}
			}
			catch (Exception e) {
				Log.exception(e);
			}
			finally {
				Util.close(out);
			}
		}
		Picture pic = new Picture(dimension.width, dimension.height);
		Iterator it = newFrameList.iterator();
		while (it.hasNext()) {
			f = (Frame)it.next();			
			pic.addFrame(f);			
		}
		pic.setCurrentFrame(0);		
		f = pic.getFrame(0);		
		pic.setTransparent(f.getTransparentColour());
		pic.setPalette(f.getPalette());		
		
		Main.setPicture(pic);
	}

	public static Picture createPicture(int aWidth, int aHeight) {
		Picture pic = new Picture(aWidth, aHeight);
		pic.setPalette(new Palette(pic));
		pic.addFrame();
		return pic;
	}
	
	public static Picture createPictureComandi(int aWidth, int aHeight) {
		Picture pic = new Picture(aWidth, aHeight);
		pic.setPalette(new Palette(pic));
		return pic;
	}

	public boolean crop(int x, int y, int w, int h) {
		if ((x != 0) || (y != 0) || (w != getWidth()) || (h != getHeight())) {
			Log.debug("cropping to " + x + "," + y + "," + w + "x" + h);
			final int frameCount = getFrameCount();
			for (int i = 0; i < frameCount; i++) {
				getFrame(i).crop(x, y, w, h);
			}
			width = w;
			height = h;
			notifyDataListeners(new ChangeEvent(this, PICTURE_SIZE_CHANGED));
			return true;
		}
		return false;
	}

	public Frame addFrame() {
		return addFrame(frames.size());
	}

	public void removeAlphaChannel() {
		surpressGUIEvents(this);
		Palette[] pals = getPalettes();
		final int len = pals.length;
		for (int i = 0; i < len; i++) {
			pals[i].removeAlphaValues();
		}
		allowGUIEvents(this);
	}

	public void addFrame(Frame aFrame) {
		frames.add(aFrame);
		notifyDataListeners(
			new ChangeEvent(this, PICTURE_FRAME_ADDED, getFrameCount() - 1));
	}

	public Frame addFrame(int index) {
		Frame frame = new Frame(this);
		frames.add(index, frame);
		if (transparent < palette.size()) {
			frame.setPalette(null, transparent);
		}
		else {
			frame.setPalette(null, -1);
		}
		notifyDataListeners(new ChangeEvent(this, PICTURE_FRAME_ADDED, index));
		setCurrentFrame(index);
		return frame;
	}

	// bringt die paletten auf eine einheitliche gr��e
	private void adjustPalettes() {
		final int maxSize = getMaximalPaletteSize();
		final int frameCount = getFrameCount();
		Palette p;
		for (int i = 0; i < frameCount; i++) {
			p = getFrame(i).getPalette();
			while (p.size() < maxSize) {
				p.addQuiet(Color.black);
			}
		}
	}

	// entfernt nicht genutzte farben
	public int compress() {
		surpressGUIEvents(this);
		final int oldCount = countColours();
		Palette[] pals = getPalettes();

		boolean changed = false;
		final int palCount = pals.length;
		boolean hasChanged;
		Palette p;
		do {
			hasChanged = false;
			for (int i = 0; i < palCount; i++) {
				p = pals[i];
				hasChanged = p.compress(findFramesWithPalette(p)) || hasChanged;
				changed = hasChanged || changed;
				// achtung! reihenfolge!
			}
		}
		while (hasChanged);

		if (changed) {
			adjustPalettes();
			allowGUIEvents(this);
			return oldCount - countColours();
		}
		else {
			allowGUIEvents(this);
			return 0;
		}
	}

	private final boolean containsPalette(ArrayList list, Palette p) {
		final int size = list.size();
		for (int i = 0; i < size; i++) {
			if (list.get(i) == p) {
				return true;
			}
		}
		return false;
	}

	public void show() {
		if (Log.DEBUG) {
			Log.debug("Picture: " + width + "x" + height);
			Log.debug("Colours: " + getMaximalPaletteSize());
			Log.debug(new ColourSettings(this).toString());
		}
	}

	private int countColours() {
		Palette[] pals = getPalettes();
		final int palCount = pals.length;
		int colours = 0;
		for (int i = 0; i < palCount; i++) {
			colours = colours + pals[i].size();
		}
		return colours;
	}

	protected Frame[] findFramesWithGlobalPalette() {
		return findFramesWithPalette(palette);
	}

	protected Frame[] findFramesWithPalette(Palette pal) {
		ArrayList list = new ArrayList();
		final int frameCount = getFrameCount();
		Frame f;
		for (int i = 0; i < frameCount; i++) {
			f = getFrame(i);
			if (f.getPalette() == pal) {
				list.add(f);
			}
		}
		Frame[] frameList = new Frame[list.size()];
		list.toArray(frameList);
		return frameList;
	}

	public int getBackground() {
		return background;
	}

	public Frame getCurrentFrame() {
		return getFrame(currentFrame);
	}

	public int getCurrentFrameIndex() {
		return currentFrame;
	}

	public Palette getCurrentPalette() {
		return getCurrentFrame().getPalette();
	}

	public int getForeground() {
		return foreground;
	}

	public Frame getFrame(int index) {
		return (Frame) frames.get(index);
	}

	public int getFrameCount() {
		return frames.size();
	}

	protected int getGlobalTransparent() {
		return transparent;
	}

	public int getHeight() {
		return height;
	}

	public int getMaximalPaletteSize() {
		int maxColours = 0;
		final int frameCount = getFrameCount();
		for (int i = 0; i < frameCount; i++) {
			int size = getFrame(i).getPalette().size();
			maxColours = Math.max(maxColours, size);
		}
		return maxColours;
	}

	public Palette getPalette() {
		return palette;
	}

	private Palette[] getPalettes() {
		ArrayList list = new ArrayList();
		final int frameCount = getFrameCount();
		Palette p;
		for (int i = 0; i < frameCount; i++) {
			p = getFrame(i).getPalette();
			if (!containsPalette(list, p)) {
				list.add(p);
			}
		}

		Palette[] pals = new Palette[list.size()];
		list.toArray(pals);
		return pals;
	}

	public int getPictureBackground() {
		return pictureBackground;
	}

	public int getTransparent() {
		if (getCurrentFrame().getPalette().isGlobalPalette()) {
			return transparent;
		}
		return getCurrentFrame().getTransparentColour();
	}

	public int getWidth() {
		return width;
	}

	public int indexOf(Frame aFrame) {
		final int frameCount = getFrameCount();
		for (int i = 0; i < frameCount; i++) {
			if (getFrame(i) == aFrame) {
				return i;
			}
		}
		return -1;
	}

	public int reduceColours() {
		surpressGUIEvents(this);
		int oldColourCount = countColours();

		compress();
		Palette[] pals = getPalettes();
		final int palCount = pals.length;
		for (int i = 0; i < palCount; i++) {
			pals[i].reducePalette();
		}
		allowGUIEvents(this);
		return oldColourCount - countColours();
	}

	public void removeCurrentFrame() {
		removeFrame(currentFrame);
	}

	public void removeFrame(final int index) {
		Frame f = (Frame) frames.remove(index);
		f.detach();
		notifyDataListeners(
			new ChangeEvent(Picture.this, PICTURE_FRAME_REMOVED, index));
	}

	public void setBackground(int i) {
		if (i != background) {
			int old = background;
			background = i;
			notifyDataListeners(
				new ChangeEvent(
					this,
					PICTURE_BACKGROUND_COLOUR_CHANGED,
					old,
					background));
		}
	}

	public void setCurrentFrame(int index) {
		if (index != currentFrame) {
			int old = currentFrame;
			if ((currentFrame != -1) && (currentFrame < getFrameCount())) {
				getFrame(currentFrame).leave();
			}
			currentFrame = index;
			getFrame(currentFrame).enter();
			notifyDataListeners(
				new ChangeEvent(this, PICTURE_FRAME_SET, old, currentFrame));
		}
	}

	public void setForeground(int i) {
		if (i != foreground) {
			int old = foreground;
			foreground = i;
			notifyDataListeners(
				new ChangeEvent(
					this,
					PICTURE_FOREGROUND_COLOUR_CHANGED,
					old,
					foreground));
		}
	}

	public void setPalette(Palette aPalette) {
		palette = aPalette;
		Frame[] frameList = findFramesWithGlobalPalette();
		final int count = frameList.length;

		for (int i = 0; i < count; i++) {
			frameList[i].setPalette(null, transparent);
		}
		notifyDataListeners(new ChangeEvent(this, PICTURE_PALETTE_CHANGED));
	}

	public void setPictureBackground(int index) {
		if (index != pictureBackground) {
			int old = pictureBackground;
			pictureBackground = index;
			notifyDataListeners(
				new ChangeEvent(this, PICTURE_MAIN_BACKGROUND_CHANGED, old, index));
		}
	}

	public void setSize(int w, int h) {
		width = w;
		height = h;

		final int frameCount = getFrameCount();
		Frame f;
		Frame newFrame;
		for (int i = 0; i < frameCount; i++) {
			f = getFrame(i);
			newFrame = new Frame(this);
			newFrame.pasteClip(f, 0, 0);
			f.setData(newFrame.getData());
		}
		notifyDataListeners(new ChangeEvent(this, PICTURE_SIZE_CHANGED));
	}

	public void setTransparent(int i) {
		if (getCurrentFrame().getPalette().isGlobalPalette()) {
			transparent = i;
		}
		getCurrentFrame().setTransparentColour(i);
	}

	public boolean usesGlobalPalette() {
		final int frameCount = getFrameCount();
		Frame f;
		for (int i = 0; i < frameCount; i++) {
			f = getFrame(i);
			if (f.getPalette().equals(palette)) {
				return true;
			}
		}
		return false;
	}

}
