package jdraw.data;

import jdraw.data.event.ChangeEvent;

import java.awt.Color;
import java.io.Serializable;

import util.Log;

/*
 * Palette.java - created on 27.10.2003
 * 
 * @author Michaela Behling
 */

public final class ColourEntry
	extends DataObject
	implements Serializable, Comparable {

	private static long serialVersionUID = 0L;

	// bit 6,7
	public static final int ALPHA_MASK = 64 + 128;
	// bit 5,6
	public final static int RED_MASK = 16 + 32;
	// bit 3,4
	public final static int GREEN_MASK = 4 + 8;
	// bit 1,2
	public final static int BLUE_MASK = 1 + 2;

	public final static int MAX_VALUE = 255;
	public final static int TRANSPARENT = 0;
	public final static int OPAQUE = MAX_VALUE;

	private Color colour;
	private int index;
	private boolean isTransparent = false;
	private int r, g, b, a;
	// wird zur kompression einer palette benötigt.
	transient private int used = 0;

	public ColourEntry(Color aColour, int anIndex) {
		index = anIndex;
		isTransparent = false;
		setColour(aColour);
	}

	public Color getColour() {
		return colour;
	}

	public Color getOpaqueColour() {
		if (a == OPAQUE) {
			return colour;
		}
		return new Color(r, g, b);
	}

	public void removeAlpha() {
		String oldCol = getColourString();
		setColour(new Color(r, g, b));
		String newCol = getColourString();
		notifyDataListeners(
			new ChangeEvent(this, ENTRY_RGBA_CHANGED, oldCol, newCol));
	}

	public int getIndex() {
		return index;
	}

	public boolean isOpaque() {
		return (!isTransparent) && (colour.getAlpha() == TRANSPARENT);
	}

	public boolean isSimilar(ColourEntry e, int diff) {

		int rDiff = Math.abs(r - e.r);
		int gDiff = Math.abs(g - e.g);
		int bDiff = Math.abs(b - e.b);
		int aDiff = Math.abs(a - e.a);

		return (rDiff <= diff)
			&& (gDiff <= diff)
			&& (bDiff <= diff)
			&& (aDiff <= diff);
	}

	public void setIndex(int anIndex) {
		if (index == anIndex) {
			return;
		}
		int oldIndex = index;
		index = anIndex;
		if (index == -1) {
			notifyDataListeners(
				new ChangeEvent(this, ENTRY_DISPOSED, oldIndex, index));
		}
		else {
			notifyDataListeners(
				new ChangeEvent(this, ENTRY_REINDEXED, oldIndex, index));
		}
	}

	public int getUsedCount() {
		return used;
	}

	public void dispose() {
		setIndex(-1);
	}

	public void decreaseIndex() {
		setIndex(index - 1);
	}

	public boolean isUsed() {
		return (used > 0);
	}

	public void increaseUsed() {
		if (isValid()) {
			used++;
		}
	}

	public void addUsed(int amount) {
		used = used + amount;
	}

	public void reset() {
		if (isValid()) {
			used = 0;
		}
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	protected void setTransparency(boolean flag) {
		if (flag != isTransparent) {
			String oldCol = getColourString();
			isTransparent = flag;
			setColour(new Color(r, g, b));
			String newCol = getColourString();
			notifyDataListeners(
				new ChangeEvent(this, ENTRY_RGBA_CHANGED, oldCol, newCol));			
		}
	}

	protected void setColour(Color c) {
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		a = isTransparent ? OPAQUE : c.getAlpha();
		colour = new Color(r, g, b, a);
		if (isTransparent && (c.getAlpha() != OPAQUE)) {
			Log.warning(
				"The alpha value of the palette's transparent colour mustn't "
					+ "be changed.\n\n<font color=blue><b>Hint</b></font>: "
					+ "Ctrl-left-click this colour to remove its "
					+ "transparent colour status. The alpha value can "
					+ "then be modified.");
		}
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Color) {
			return colour.equals(o);
		}
		if (!(o instanceof ColourEntry)) {
			return false;
		}
		ColourEntry e = (ColourEntry) o;

		return hashCode() == e.hashCode();
	}

	protected int sum() {
		return r + g + b + a;
	}

	public int compareTo(Object o) {
		ColourEntry e = (ColourEntry) o;

		// transparente > deckende farben
		if (a != e.a) {
			if (a > e.a) {
				return 1;
			}
			else {
				return -1;
			}
		}
		float[] hsb =
			Color.RGBtoHSB(
				colour.getRed(),
				colour.getGreen(),
				colour.getBlue(),
				null);
		float[] ehsb =
			Color.RGBtoHSB(
				e.colour.getRed(),
				e.colour.getGreen(),
				e.colour.getBlue(),
				null);

		for (int i = 0; i < 3; i++) {
			if (hsb[i] < ehsb[i])
				return -1;
			else if (hsb[i] > ehsb[i]) {
				return 1;
			}
		}
		if (index == e.index) {
			return 0;
		}
		else if (index < e.index) {
			return -1;
		}
		else {
			return 1; // index > e.index
		}
	}

	private String hashString() {
		float[] hsb =
			Color.RGBtoHSB(
				colour.getRed(),
				colour.getGreen(),
				colour.getBlue(),
				null);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			buf.append(hsb[i]);
			buf.append('.');
		}
		buf.append(a);
		return buf.toString();
	}

	protected final String getColourString() {
		return getColourString(r, g, b, isTransparent ? 0 : a);
	}

	protected static final String getColourString(
		int red,
		int green,
		int blue,
		int alpha) {
		StringBuffer buf = new StringBuffer();
		buf.append(red);
		buf.append('.');
		buf.append(green);
		buf.append('.');
		buf.append(blue);
		buf.append('.');
		buf.append(alpha);
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("#");
		buf.append(String.valueOf(index));
		buf.append(":[");
		buf.append(r);
		buf.append(",");
		buf.append(g);
		buf.append(",");
		buf.append(b);
		buf.append(",");
		buf.append(a);
		buf.append("]");
		if (isTransparent) {
			buf.append(" (transparent)");
		}
		return buf.toString();
	}

	public int hashCode() {
		int code = hashString().hashCode();
		if (isTransparent) {
			code = code + 1000;
		}
		code = code + index;
		return code;
	}

	public void invalidate() {
		if (Log.DEBUG)
			Log.debug("invalidated colour #" + index);
		used = -1;
	}

	public boolean isValid() {
		return used >= 0;
	}
}