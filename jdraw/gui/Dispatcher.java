package jdraw.gui;

import jdraw.action.DrawAction;
import jdraw.action.RemoveColourAction;
import jdraw.action.SwapColoursAction;
import jdraw.data.ColourEntry;
import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.data.event.ChangeEvent;
import jdraw.data.event.EventConstants;
import util.Assert;
import util.Log;

/*
 * Dispatcher.java - created on 18.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class Dispatcher implements EventConstants {

	private Dispatcher() {
	}

	private static void fail(ChangeEvent e) {
		Assert.fail("gui: cannot handle " + e.toString());
	}

	private static Picture getPicture() {
		return Tool.getPicture();
	}

	public static void dispatch(ChangeEvent e) {
		if (Log.DEBUG)
			Log.debug("-- dispatch " + e.toString());
		switch (e.changeType) {
			//	Clip
			case CLIP_DATA_CHANGED :
			case CLIP_PIXEL_CHANGED :
				dispatchClipEvent(e);
				break;
				// Frame
			case FRAME_NEEDS_REPAINT :
			case FRAME_USES_NEW_PALETTE :
			case FRAME_TRANSPARENT_COLOUR_CHANGED :
				dispatchFrameEvent(e);
				break;
				// Palette
			case PALETTE_CHANGED :
			case PALETTE_COLOUR_CHANGED :
			case PALETTE_COLOUR_ADDED :
				dispatchPaletteEvent(e);
				break;
				// Picture
			case PICTURE_FRAME_ADDED :
			case PICTURE_FRAME_REMOVED :
			case PICTURE_FRAME_SET :
			case PICTURE_PALETTE_CHANGED :
			case PICTURE_SIZE_CHANGED :
			case PICTURE_CHANGED :
			case PICTURE_MAIN_BACKGROUND_CHANGED :
			case PICTURE_FOREGROUND_COLOUR_CHANGED :
			case PICTURE_BACKGROUND_COLOUR_CHANGED :
				dispatchPictureEvent(e);
				break;
				// ColourEntry
			case ENTRY_REINDEXED :
			case ENTRY_DISPOSED :
			case ENTRY_RGBA_CHANGED :
				dispatchColourEntryEvent(e);
				break;
			default :
				fail(e);
		}
	}

	private static void dispatchPictureEvent(ChangeEvent e) {
		switch (e.changeType) {
			case PICTURE_BACKGROUND_COLOUR_CHANGED :
			case PICTURE_FOREGROUND_COLOUR_CHANGED :
				PalettePanel.INSTANCE.changeColour(e);
				DrawAction.getAction(SwapColoursAction.class).setEnabled(
					getPicture().getForeground() != getPicture().getBackground());
				break;
			case PICTURE_MAIN_BACKGROUND_CHANGED :
				PalettePanel.INSTANCE.changeColour(e);
				Tool.getCurrentFramePanel().changeMainBackground(e);
				break;
			case PICTURE_FRAME_ADDED :
				Frame f = Tool.getCurrentFrame();
				f.addDataChangeListener(MainFrame.INSTANCE);
				FolderPanel.INSTANCE.changeFrameAdded(e);
				break;
			case PICTURE_FRAME_SET :
				FolderPanel.INSTANCE.changeFrameSet(e);
				break;
			case PICTURE_FRAME_REMOVED :
				FolderPanel.INSTANCE.changeFrameRemoved(e);
				break;
			case PICTURE_CHANGED :
				MainFrame.INSTANCE.setPicture(Tool.getPicture());
				break;
			case PICTURE_SIZE_CHANGED :
				MainFrame.INSTANCE.updateTitle();
				DrawPanel p = Tool.getDrawPanel();
				p.setPreferredSize(p.getPreferredSize());
				PreviewPanel.INSTANCE.setPreferredSize(
					PreviewPanel.INSTANCE.getPreferredSize());
				break;
			case PICTURE_PALETTE_CHANGED :
			default :
				fail(e);
		}
	}

	private static void dispatchPaletteEvent(ChangeEvent e) {		
		switch (e.changeType) {
			case PALETTE_COLOUR_ADDED :
				PalettePanel.INSTANCE.colourAdded((ColourEntry) e.getNewValue());
				MainFrame.INSTANCE.updateTitle();
				break;
			case PALETTE_COLOUR_CHANGED :
				PalettePanel.INSTANCE.changeColour(e);
				break;
			case PALETTE_CHANGED :				
				PalettePanel.INSTANCE.setPalette((Palette) e.source);
				DrawAction.getAction(RemoveColourAction.class).setEnabled(
					getPicture().getCurrentPalette().size() > 2);
				break;
			default :
				fail(e);
		}
	}

	private static void dispatchClipEvent(ChangeEvent e) {
		switch (e.changeType) {
			case CLIP_PIXEL_CHANGED :
				Tool.getCurrentFramePanel().changePixel(e);
				break;
			case CLIP_DATA_CHANGED :
				Tool.getCurrentFramePanel().changeClipData(e);
				break;
			default :
				fail(e);
		}
	}

	private static void dispatchFrameEvent(ChangeEvent e) {
		switch (e.changeType) {
			case FRAME_NEEDS_REPAINT :
				Tool.getCurrentFramePanel().changeNeedsRepaint(e);
				break;
			case FRAME_TRANSPARENT_COLOUR_CHANGED :
				PalettePanel.INSTANCE.changeColour(e);
				Tool.getCurrentFramePanel().changeNeedsRepaint(e);
				break;
			case FRAME_USES_NEW_PALETTE :
				Palette p = Tool.getCurrentPalette();
				Tool.getCurrentFramePanel().changeNeedsRepaint(e);
				PalettePanel.INSTANCE.setPalette(p);
				p.addDataChangeListener(MainFrame.INSTANCE);
				break;
			default :
				fail(e);
		}
	}

	private static void dispatchColourEntryEvent(ChangeEvent e) {
		switch (e.changeType) {
			case ENTRY_REINDEXED :
			case ENTRY_DISPOSED :
			case ENTRY_RGBA_CHANGED :
			default :
				fail(e);
		}
	}

}
