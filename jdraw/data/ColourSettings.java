package jdraw.data;

import java.io.Serializable;

import util.Assert;
import util.Log;

/*
 * ColourSettings.java - created on 20.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class ColourSettings implements Serializable {
	public final ColourEntry foreground;
	public final ColourEntry background;
	public final ColourEntry transparent;
	public final ColourEntry pictureBackground;
	public final Picture picture;

	public ColourSettings(Picture aPicture) {
		picture = aPicture;
		Palette pal = picture.getCurrentPalette();
		foreground = pal.getColour(picture.getForeground());
		background = pal.getColour(picture.getBackground());
		pictureBackground = pal.getColour(picture.getPictureBackground());
		final int t = picture.getTransparent();
		if (t == -1) {
			transparent = null;
		}
		else {
			transparent = pal.getColour(t);
		}
	}

	public void restore() {
		if (Log.DEBUG)
			Log.debug(">>> restoring colour settings");
		int index;
		Palette pal = picture.getCurrentPalette();
		//	foreground reindexed?		
		index = pal.indexOf(foreground);
		if (index == -1) {
			index = 1;
		}
		picture.setForeground(index);
		//	background reindexed?
		index = pal.indexOf(background);
		if (index == -1) {
			index = 0;
		}
		picture.setBackground(index);
		//	picture background reindexed?
		index = pal.indexOf(pictureBackground);
		if (index == -1) {
			index = 0;
		}
		picture.setPictureBackground(index);

		//	transparent changed?
		int currentTrans = picture.getTransparent();
		index = -1;
		if (transparent != null) { // es existierte keine trans colour
			index = transparent.getIndex();
		}
		if (currentTrans != index) {
			if (currentTrans == -1) { // trans colour ist gelöscht worden			
				picture.setTransparent(-1);
			}
			else if (index == -1) { // eine neue trans colour wurde gesetzt
				picture.setTransparent(currentTrans);
			}
			else { // trans colour wurde reindexed
				picture.setTransparent(index);
			}
		}

		if (Log.DEBUG) {
			Log.debug("After restoring:");
			Log.debug(
				toString(
					pal.getColour(picture.getForeground()),
					pal.getColour(picture.getBackground()),
					pal.getColour(picture.getPictureBackground()),
					(picture.getTransparent() == -1)
						? null
						: pal.getColour(picture.getTransparent())));
		}
	}

	private String toString(
		ColourEntry fore,
		ColourEntry back,
		ColourEntry pictureBack,
		ColourEntry trans) {
		StringBuffer buf = new StringBuffer();
		buf.append("\nforeground:         ");
		buf.append(fore);
		buf.append("\nbackground:         ");
		buf.append(back);
		buf.append("\ntransparent:        ");
		buf.append((trans == null) ? "none" : trans.toString());
		buf.append("\npicture background: ");
		buf.append(pictureBack);
		return buf.toString();
	}

	public String toString() {
		return toString(foreground, background, pictureBackground, transparent);
	}
}
