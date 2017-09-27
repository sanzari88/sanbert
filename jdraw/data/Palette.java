package jdraw.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import jdraw.data.event.ChangeEvent;
import util.Assert;
import util.Log;

/*
 * Palette.java - created on 27.10.2003
 * 
 * @author Michaela Behling
 */

public final class Palette extends DataObject implements DataChangeListener {

	private static long serialVersionUID = 0L;

	public static final int GIF_MAX_COLOURS = 256;
	private boolean sorting = false;

	private final ColourList colours = new ColourList();

	private final Picture picture;
	private final HashMap colourMap = new HashMap(GIF_MAX_COLOURS);
	
	public static HashMap <Integer,String> tabellaColori = new HashMap<>();
	private int letteraASCI = 96;

	public Palette(Picture aPicture) {
		picture = aPicture;
	}
	
	public static HashMap <Integer,String>  getTabellaColoriMaglia() {
		return tabellaColori;
	}

	public static Palette createDefaultPalette(Picture aPicture) {

		Palette pal = new Palette(aPicture);

//		for (int i = 0; i < 8; i++) {
//			pal.addQuiet(Color.black);
//		}
		pal.addQuiet(Color.black);
		
		final int start = 255;
		final int step = -26;
		final int max = 1;
		//	rot
		int b = start;
		for (int i = 0; i < max; i++) {
			Color c = new Color(b, 0, 0);
			pal.addQuiet(c);
			b = b + step;
		}
		// gr�n
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(0, b, 0));
			b = b + step;
		}
		// blau
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(0, 0, b));
			b = b + step;
		}
		// t�rkis
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(0, b, b));
			b = b + step;
		}
		// gelb
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(b, b, 0));
			b = b + step;
		}
		// orange
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(b, b / 2, 0));
			b = b + step;
		}
		//	violett
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(b, 0, b));
			b = b + step;
		}
		// grau
		b = start;
		for (int i = 0; i < max; i++) {
			pal.addQuiet(new Color(b, b, b));
			b = b + step;
		}
		return pal;
	}

	public int countTransparentColours() {
		final int size = size();
		int count = 0;
		if (picture.getTransparent() != -1) {
			count++;
		}
		for (int i = 0; i < size; i++) {
			if (getColour(i).getColour().getAlpha() != ColourEntry.OPAQUE) {
				count++;
			}
		}
		return count;
	}

	public int getLastTransparentColour() {
		final int size = size();
		Color col;
		final int trans = picture.getTransparent();
		for (int i = size - 1; i >= 0; i--) {
			if (i == trans) {
				return trans;
			}
			col = getColour(i).getColour();
			if (col.getAlpha() != ColourEntry.OPAQUE) {
				return i;
			}
		}
		return -1;
	}

	public void addColour(Color col) {
		ColourEntry e = addQuiet(col);
		notifyDataListeners(new ChangeEvent(this, PALETTE_COLOUR_ADDED, null, e));
	}

	public ColourEntry addQuiet(Color col) {
		ColourEntry e = new ColourEntry(col, size());
		tabellaColori.put(e.getIndex(), Character.toString ((char) letteraASCI++));
		colours.add(e);
		return e;
	}

	public void removeAlphaValues() {
		surpressGUIEvents(picture);
		final int size = size();
		ColourEntry e;
		for (int i = 0; i < size; i++) {
			e = getColour(i);
			if (!(e.isTransparent() || e.isOpaque())) {
				e.removeAlpha();
			}
		}
		allowGUIEvents(picture);
	}

	public void purgeColours() {
		surpressGUIEvents(picture);
		ArrayList list = new ArrayList(colours);
		final int size = size();
		ColourEntry e;
		for (int i = 0; i < size; i++) {
			e = (ColourEntry) list.get(i);
			if (!e.isUsed()) {
				e.dispose();
			}
		}
		allowGUIEvents(picture);
	}

	protected void updateTransparent(int oldIndex, int newIndex) {
		Frame[] frames = picture.findFramesWithPalette(this);
		final int count = frames.length;
		for (int i = 0; i < count; i++) {
			frames[i].setTransparentColourQuiet(newIndex);
		}
		if (oldIndex != -1) {
			getColour(oldIndex).setTransparency(false);
		}
		if (newIndex != -1) {
			getColour(newIndex).setTransparency(true);
		}
	}

	public int compress() {
		int oldSize = size();
		compress(picture.findFramesWithPalette(this));
		return oldSize - size();
	}

	protected boolean compress(Frame[] frames) {
		if (Log.DEBUG)
			Log.debug("compressing palette...");
		// kompliziert geraten, um die reihenfolge der farben zu erhalten
		final int oldSize = size();
		resetCounters();
		final int width = picture.getWidth();
		final int height = picture.getHeight();
		final int frameCount = frames.length;
		Frame f;
		for (int i = 0; i < frameCount; i++) {
			f = frames[i];
			// benutzte farben finden	
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					getColour(f.getPixel(x, y)).increaseUsed();
				}
			}
		}
		purgeColours();
		final int newSize = size();
		// hat sich was ge�ndert?
		boolean changed = (oldSize != newSize);
		if (Log.DEBUG)
			Log.debug("done.");
		return changed;
	}

	public Palette copy() {
		Palette p = new Palette(picture);
		final int size = size();
		for (int i = 0; i < size; i++) {
			p.colours.add(colours.get(i));
		}
		return p;
	}

	public final void dataChanged(ChangeEvent e) {
		switch (e.changeType) {
			case ENTRY_DISPOSED :
				entryDisposed(e);
				break;
			case ENTRY_REINDEXED :
				entryReindexed(e);
				break;
			case ENTRY_RGBA_CHANGED :
				rehash(e);
				notifyDataListeners(
					new ChangeEvent(
						this,
						PALETTE_COLOUR_CHANGED,
						((ColourEntry) e.source).getIndex()));
				break;
			default :
				Assert.fail("data: cannot handle event " + e.toString());
		}
	}

	private void rehash(ChangeEvent e) {
		String oldHash = (String) e.getOldValue();
		String newHash = (String) e.getNewValue();
		//Log.debug(">> " + oldHash + " -> " + newHash);
		colourMap.remove(oldHash);
		colourMap.put(newHash, e.source);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Palette)) {
			return false;
		}
		Palette p = (Palette) o;
		final int size = size();
		if (p.size() != size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!getColour(i).equals(p.getColour(i))) {
				return false;
			}
		}
		return true;
	}

	public final int hashCode() {
		// korrespondiert nicht zu equals!
		return super.hashCode();
	}

	public int findColour(Color col) {
		String key =
			ColourEntry.getColourString(
				col.getRed(),
				col.getGreen(),
				col.getBlue(),
				col.getAlpha());
		ColourEntry e = (ColourEntry) colourMap.get(key);
		if (e == null) {
			return -1;
		}
		return e.getIndex();
		//		final int size = size();
		//		ColourEntry e;
		//		for (int i = 0; i < size; i++) {
		//			e = getColour(i);
		//			if (e.equals(col)) {
		//				return i;
		//			}
		//		}
		//		return -1;

	}

	public ColourEntry getColour(int index) {
		return (ColourEntry) colours.get(index);
	}

	public Picture getPicture() {
		return picture;
	}

	public int indexOf(ColourEntry col) {
		return colours.indexOf(col);
	}

	public boolean isGlobalPalette() {
		//	achtung: beim speichern zum optimieren auf equals pr�fen!
		return picture.getPalette() == this;
	}

	public int reduceColours() {
		surpressGUIEvents(picture);
		int freed = reducePalette();
		allowGUIEvents(picture);
		return freed;
	}

	protected int reducePalette() {
		int oldSize = size();
		resetCounters();
		Frame[] frames = picture.findFramesWithPalette(this);
		final int count = frames.length;
		Frame f;
		final int w = picture.getWidth();
		final int h = picture.getHeight();
		// colour usage feststellen
		for (int i = 0; i < count; i++) {
			f = frames[i];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					getColour(f.getPixel(x, y)).increaseUsed();
				}
			}
		}
		purgeColours();
		int used = 1;
		Log.debug("used=" + used);
		int size = size();
		while (size > GIF_MAX_COLOURS) {
			boolean changed = false;
			ColourEntry rep;
			ColourEntry e;

			for (int i = 0; i < size; i++) {
				e = getColour(i);
				if (e.getUsedCount() == used) {
					rep = findSimilarColour(i);
					replaceColour(i, rep.getIndex());
					rep.addUsed(used);
					e.reset();
					changed = true;
					break;
				}
			}
			if (!changed) {
				used++;
				Log.debug("used = " + used);
			}
			else {
				size--;
			}
		}
		updatePalette();
		return size() - oldSize;
	}

	private void updatePalette() {
		int count = 0;
		Iterator cols = new ArrayList(colours).iterator();
		ColourEntry e;
		while (cols.hasNext()) {
			e = (ColourEntry) cols.next();
			if (e.isUsed()) {
				e.setIndex(count);
				count++;
			}
			else {
				colours.remove(e);
			}
		}
		if (Log.DEBUG) {
			Log.debug(">> palette size after reducing: " + size());
		}
	}

	private ColourEntry findSimilarColour(int index) {
		int diff = 0;
		final int size = size();
		ColourEntry entry = getColour(index);
		ColourEntry e;
		while (true) {
			for (int i = 0; i < size; i++) {
				if (i != index) {
					e = getColour(i);
					if (e.isUsed() && e.isSimilar(entry, diff)) {
						return e;
					}
				}
			}
			diff++;
		}
	}

	public ColourEntry findMaxUsedColorEntryForColour(Color col) {
		ColourEntry entry = null;
		ColourEntry e;
		Iterator it = colours.iterator();
		while (it.hasNext()) {
			e = (ColourEntry) it.next();
			if (e.getColour().equals(col)) {
				if ((entry == null) || (entry.getUsedCount() < e.getUsedCount())) {
					entry = e;
				}
			}
		}
		return entry;
	}

	private void reindex(int startIndex) {
		final int size = size();
		if (startIndex < size) { // farben nach #index umindexieren			
			ColourEntry entry;
			for (int i = startIndex + 1; i < size; i++) {
				entry = getColour(i);
				entry.decreaseIndex();
			}
		}
	}

	private void replaceIndices(int[] newIndices) {
		Frame[] frames = picture.findFramesWithPalette(this);
		final int size = frames.length;
		for (int i = 0; i < size; i++) {
			frames[i].replaceColours(newIndices);
		}
	}

	private void replaceIndex(int oldIndex, int newIndex) {
		Frame[] frames = picture.findFramesWithPalette(this);
		final int size = frames.length;
		for (int i = 0; i < size; i++) {
			frames[i].replaceColour(oldIndex, newIndex);
		}
	}

	private void entryReindexed(ChangeEvent e) {
		if (!sorting) {
			replaceIndex(e.getOldInt(), e.getNewInt());
		}
	}

	private void entryDisposed(ChangeEvent e) {
		ColourEntry entry = (ColourEntry) e.source;
		if (entry.isTransparent()) {
			picture.setTransparent(-1);
		}
		final int oldIndex = e.getOldInt();
		replaceIndex(oldIndex, 0);
		colours.remove(oldIndex);
		reindex(oldIndex);
		// minimale groesse sicherstellen
		if (size() == 1) {
			Color c = getColour(0).getColour();
			if (c.getRed() + c.getGreen() + c.getBlue() == 0) {
				colours.add(new ColourEntry(Color.white, 1));
			}
			else {
				colours.add(new ColourEntry(Color.black, 1));
			}
		}
		notifyDataListeners(new ChangeEvent(this, PALETTE_CHANGED));
	}

	public void removeColour(int index) {
		getColour(index).dispose();
	}

	public void replaceColour(int currentColour, int newColour) {
		if (currentColour != newColour) {
			Frame[] frames = picture.findFramesWithPalette(this);
			final int count = frames.length;
			for (int i = 0; i < count; i++) {
				frames[i].replaceColour(currentColour, newColour);
			}
		}
	}

	private void resetCounters() {
		final int size = size();
		for (int i = 0; i < size; i++) {
			getColour(i).reset();
		}
	}

	public void setColour(int index, Color col) {
		setQuiet(index, col);
		if (col.getAlpha() == ColourEntry.TRANSPARENT) {
			picture.setTransparent(index);
		}
		notifyDataListeners(new ChangeEvent(this, PALETTE_COLOUR_CHANGED, index));
	}

	public void setQuiet(int index, Color col) {
		final int size = size();
		if (index < size) {
			ColourEntry e = getColour(index);
			e.setColour(col);
		}
		else {
			ColourEntry e = new ColourEntry(col, Math.min(index, size));
			colours.set(index, e);
		}
	}

	private void setTransparency(int index, boolean isTransparent) {
		if (index != -1) {
			ColourEntry e = getColour(index);
			e.setTransparency(isTransparent);
		}
	}

	public int size() {
		return colours.size();
	}

	private void clearColours() {
		while (colours.size() > 0) {
			colours.remove(0);
		}
	}

	public void sort() {
		surpressGUIEvents(picture);
		sorting = true;
		TreeSet set = new TreeSet(colours);
		Assert.isTrue(set.size() == size(), "wrong hashcode/equals functions");
		Iterator it = set.iterator();
		clearColours();
		ColourEntry entry;
		int index = 0;
		int[] newIndices = new int[set.size()];
		while (it.hasNext()) {
			entry = (ColourEntry) it.next();
			colours.add(entry);
			newIndices[entry.getIndex()] = index;
			entry.setIndex(index);
			index++;
		}
		replaceIndices(newIndices);
		sorting = false;
		allowGUIEvents(picture);
	}

	private void show() {
		if (Log.DEBUG) {
			Log.debug("\nPalette (" + size() + " colours)");
			for (int i = 0; i < size(); i++) {
				Log.debug("   " + getColour(i).toString());
			}
		}
	}

	public void swapColours(Picture pic, int colour1, int colour2) {
		Frame[] frames = pic.findFramesWithPalette(this);
		final int count = frames.length;
		for (int i = 0; i < count; i++) {
			frames[i].swapColours(colour1, colour2);
		}
	}

	// ColourList
	private final class ColourList extends ArrayList {

		public void add(int index, Object o) {
			attach(o);
			super.add(index, o);
		}

		public boolean add(Object o) {
			attach(o);
			return super.add(o);
		}

		public boolean addAll(Collection c) {
			Assert.fail("data: method forbidden");
			return super.addAll(c);
		}

		public boolean addAll(int index, Collection c) {
			Assert.fail("data: method forbidden");
			return super.addAll(index, c);
		}

		private void attach(Object o) {
			check(o);
			ColourEntry e = (ColourEntry) o;
			colourMap.put(e.getColourString(), e);
			e.addDataChangeListener(Palette.this);
		}

		private void check(Object o) {
			//			Assert.isTrue(
			//				o instanceof ColourEntry,
			//				"data: ColourEntry expected instead of " + o.getClass().getName());
		}

		public void clear() {
			Assert.fail("data: method forbidden");
			super.clear();
		}

		private void detach(Object o) {
			check(o);
			//Assert.isTrue(contains(o), "data: can't detach from colour");
			ColourEntry e = (ColourEntry) o;
			colourMap.remove(e);
			e.removeDataChangeListener(Palette.this);
		}

		public Object remove(int index) {
			detach(get(index));
			Object o = super.remove(index);
			return o;
		}

		public boolean remove(Object o) {
			detach(o);
			return super.remove(o);
		}

		protected void removeRange(int fromIndex, int toIndex) {
			Assert.fail("data: method forbidden");
			super.removeRange(fromIndex, toIndex);
		}

		public Object set(int index, Object o) {
			attach(o);
			return super.set(index, o);
		}
	}
}