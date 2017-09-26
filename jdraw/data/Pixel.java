package jdraw.data;

import java.awt.Point;

import util.Log;

/*
 * Pixel.java - created on 15.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class Pixel {
	public final int x;
	public final int y;
	public final int oldColour;
	public final int newColour;

	public Pixel(int xx, int yy, int oldCol, int newCol) {
		this.x = xx;
		this.y = yy;
		this.oldColour = oldCol;
		this.newColour = newCol;
	}

	public void paint(Frame frame) {
		frame.setPixel(x, y, newColour);
	}

	public void restore(Frame frame) {
		frame.setPixel(x, y, oldColour);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public boolean equals(Object o) {
		return hashCode() == o.toString().hashCode();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getName());
		buf.append("(");
		buf.append(String.valueOf(x));
		buf.append('.');
		buf.append(String.valueOf(y));
		buf.append(".#");
		buf.append(String.valueOf(oldColour));
		buf.append(".#");
		buf.append(String.valueOf(newColour));
		buf.append(')');
		return buf.toString();
	}
}
