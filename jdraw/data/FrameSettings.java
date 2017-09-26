package jdraw.data;

import java.io.Serializable;

/*
 * FrameSettings.java - created on 03.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class FrameSettings implements Serializable {

	public static final int DISPOSAL_UNDEFINED = 0;
	public static final int DISPOSAL_NONE = 1;
	public static final int DISPOSAL_RESTORE_BACKGROUND = 2;
	public static final int DISPOSAL_RESTORE_PREVIOUS = 3;

	private int delay = 100;
	private int disposalMethod = 0;
	private int iconWidth;
	private int iconHeight;

	public FrameSettings() {
	}
	
	public int getDelay() {
		return delay;
	}

	public int getDisposalMethod() {
		return disposalMethod;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public void setDelay(int i) {
		delay = i;
	}

	public void setDisposalMethod(int i) {
		disposalMethod = i;
	}

	public void setIconHeight(int i) {
		iconHeight = i;
	}

	public void setIconWidth(int i) {
		iconWidth = i;
	}
	
	
	public void setFrom(FrameSettings settings) {
		delay = settings.delay;
		disposalMethod = settings.disposalMethod;
		iconWidth = settings.iconWidth;
		iconHeight = settings.iconHeight;
	}

}
