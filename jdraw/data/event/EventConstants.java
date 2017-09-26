package jdraw.data.event;

/**
 * Created on 02-Nov-2003 by J-Domain
 * @author michaela
 */

public interface EventConstants {

	public static final String[] EVENT_NAMES = {
		//	CLIP EVENTS
		"clip: data changed", //0			
		"clip: pixel changed", //1
		"", //2
		"", //3
		"", //4
		"", //5
		"", //6
		"", //7
		"", //8
		"", //9
		// FRAME EVENTS
		"frame: needs repaint", //10
		"frame: uses new palette", //11
		"frame: transparent colour changed", //12
		"", //13
		"", //14
		"", //15
		"", //16
		"", //17
		"", //18
		"", //19
		// PALETTE EVENTS
		"pal: colours changed", //20
		"pal: colour changed", //21
		"pal: colour added", //22
		"", //23
		"", //24
		"", //25
		"", //26
		"", //27
		"", //28
		"", //29	
		// PICTURE EVENTS		
		"pic: frame added", //30		
		"pic: frame removed", //31
		"pic: frame set", //32
		"pic: palette changed", //33
		"pic: size changed", //34
		"pic: changed", //35
		"pic: picture background changed", //36
		"pic: foreground changed", //37
		"pic: background changed", //38
		"", //39	
		// COLOUR ENTRY EVENTS
		"entry: reindexed", //40	
		"entry: disposed", //41
		"entry: RGBA values changed", //42
		"", //43
		"", //44
	};

	// Clip
	public static final int CLIP_DATA_CHANGED = 0;
	public static final int CLIP_PIXEL_CHANGED = 1;

	// Frame
	public static final int FRAME_NEEDS_REPAINT = 10;
	public static final int FRAME_USES_NEW_PALETTE = 11;
	public static final int FRAME_TRANSPARENT_COLOUR_CHANGED = 12;

	// Palette
	public static final int PALETTE_CHANGED = 20;
	public static final int PALETTE_COLOUR_CHANGED = 21;
	public static final int PALETTE_COLOUR_ADDED = 22;

	// Picture
	public static final int PICTURE_FRAME_ADDED = 30;
	public static final int PICTURE_FRAME_REMOVED = 31;
	public static final int PICTURE_FRAME_SET = 32;
	public static final int PICTURE_PALETTE_CHANGED = 33;
	public static final int PICTURE_SIZE_CHANGED = 34;
	public static final int PICTURE_CHANGED = 35;
	public static final int PICTURE_MAIN_BACKGROUND_CHANGED = 36;
	public static final int PICTURE_FOREGROUND_COLOUR_CHANGED = 37;
	public static final int PICTURE_BACKGROUND_COLOUR_CHANGED = 38;

	// ColourEntry
	public static final int ENTRY_REINDEXED = 40;
	public static final int ENTRY_DISPOSED = 41;
	public static final int ENTRY_RGBA_CHANGED = 42;
}
