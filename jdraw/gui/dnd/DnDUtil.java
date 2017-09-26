package jdraw.gui.dnd;

import jdraw.gui.FrameSettingsDialog.FrameEntry;

import java.awt.datatransfer.DataFlavor;

/*
 * DnDUtil.java - created on 03.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class DnDUtil {

	public static final DataFlavor[] FRAME_FLAVORS =
		new DataFlavor[] { new DataFlavor(FrameEntry.class, "Frame")};

	private DnDUtil() {
	}

}
