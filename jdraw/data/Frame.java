package jdraw.data;

import jdraw.data.event.ChangeEvent;
import util.Assert;
import util.Log;

/*
 * Frame.java - created on 27.10.2003
 * 
 * @author Michaela Behling
 */

public final class Frame extends Clip implements DataChangeListener {

	private static long serialVersionUID = 0L;

	private final FrameSettings settings = new FrameSettings();
	private final Picture picture;
	private Palette palette = null;

	public Frame(Picture aPicture) {
		super(
			aPicture.getWidth(),
			aPicture.getHeight(),
			aPicture.getPictureBackground());
		picture = aPicture;
		settings.setIconWidth(getWidth());
		settings.setIconHeight(getHeight());
	}

	protected void crop(int minX, int minY, int w, int h) {
		int[][] newData = new int[h][w];
		for (int y = minY; y < minY + h; y++) {
			System.arraycopy(data[y], minX, newData[y - minY], 0, w);
		}
		data = newData;
		settings.setIconWidth(getWidth());
		settings.setIconHeight(getHeight());
	}

	public Frame copy(Picture aPicture) {
		return copy(aPicture, false);
	}

	public Clip createClip(int x, int y, int w, int h) {
		Clip clip = new Clip(w, h, picture.getPictureBackground());
		clip.transColour = transColour;

		for (int row = y; row < y + h; row++) {
			System.arraycopy(data[row], x, clip.data[row - y], 0, w);
		}

		return clip;
	}

	protected void attach() {
		getPalette().addDataChangeListener(this);
	}

	protected void detach() {
		getPalette().removeDataChangeListener(this);
	}

	public Frame copy(Picture aPicture, boolean copyDataOnly) {
		Frame f = new Frame(aPicture);
		final int len = getWidth();
		final int rows = getHeight();
		for (int y = 0; y < rows; y++) {
			System.arraycopy(data[y], 0, f.data[y], 0, len);
		}
		if (!copyDataOnly) {
			f.settings.setFrom(settings);
			// let's not copy the palette
			f.setPalette(palette, transColour);
		}
		return f;
	}

	public void toggleLocalPalette() {
		if (getPalette().isGlobalPalette()) { // switch to local			
			setPalette(getPalette().copy(), transColour);
		}
		else { // switch to global
			setPalette(null, picture.getGlobalTransparent());
		}
	}

	public void setPalette(Palette aPalette) {
		setPalette(aPalette, transColour);
	}

	public void setPalette(Palette aPalette, int transIndex) {
		int oldSize = 0;
		int firstMissingColour = -1;
		final Palette oldPalette = getPalette();
		if (oldPalette != null) {
			detach();
			oldSize = oldPalette.size();
			int newSize =
				(aPalette == null) ? picture.getPalette().size() : aPalette.size();
			firstMissingColour = oldSize - newSize;
		}
		palette = aPalette;
		setTransparentColour(transIndex);

		attach();

		if (firstMissingColour > 0) {
			for (int i = firstMissingColour; i < oldSize; i++) {
				replaceColour(i, 0);
			}
			if (Log.DEBUG)
				Log.debug("replaced " + firstMissingColour + " colours with #0");
		}
		notifyDataListeners(new ChangeEvent(this, FRAME_USES_NEW_PALETTE));
	}

	public void enter() {
	}

	public void leave() {
	}

	public FrameSettings getSettings() {
		return settings;
	}

	protected void replaceColour(int oldCol, int newCol) {
		replaceColour(this, this, oldCol, newCol);
	}

	private void replaceColour(
		Frame source,
		Frame destin,
		int oldCol,
		int newCol) {
		final int height = getHeight();
		final int width = getWidth();
		boolean changed = false;
		int col;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				col = source.getPixel(x, y);
				if (col == oldCol) {
					changed = true;
					destin.setPixelQuiet(x, y, newCol);
				}
			}
		}
		if (changed) {
			notifyDataListeners(new ChangeEvent(this, CLIP_DATA_CHANGED));
		}
	}

	protected void replaceColours(int[] newColours) {
		final int height = getHeight();
		final int width = getWidth();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setPixelQuiet(x, y, newColours[getPixel(x, y)]);
			}
		}
		notifyDataListeners(new ChangeEvent(this, CLIP_DATA_CHANGED));
	}

	public void addIndex(int start) {
		final int height = getHeight();
		final int width = getWidth();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setPixelQuiet(x, y, getPixel(x, y) + start);
			}
		}
	}

	protected void reindexFrom(int start) {
		final int height = getHeight();
		final int width = getWidth();
		boolean changed = false;

		int col;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				col = getPixel(x, y);
				if (col > start) {
					changed = true;
					setPixelQuiet(x, y, col - 1);
				}
			}
		}
		if (changed) {
			notifyDataListeners(new ChangeEvent(this, FRAME_NEEDS_REPAINT));
		}
	}

	protected void swapColours(int a, int b) {
		Frame copy = copy(picture, true);
		replaceColour(this, this, a, b);
		replaceColour(copy, this, b, a);
	}

	public boolean compress() {
		return getPalette().compress(new Frame[] { this });
	}

	public Palette getPalette() {
		if (palette == null) {
			return picture.getPalette();
		}
		return palette;
	}

	protected int[][] getData() {
		return data;
	}

	public void setTransparentColour(int index) {
		if (transColour != index) {
			int old = transColour;
			transColour = index;
			getPalette().updateTransparent(old, transColour);
			notifyDataListeners(
				new ChangeEvent(
					this,
					FRAME_TRANSPARENT_COLOUR_CHANGED,
					old,
					transColour));
		}
	}

	public final MinMax getDimensions() {
		MinMax mm = new MinMax();
		final int h = getHeight();
		final int w = getWidth();

		int maxX = 0;
		int maxY = 0;

		int pixel;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixel = getPixel(x, y);
				if (pixel != transColour) {
					if (x < mm.minX) {
						mm.minX = x;
					}
					if (y < mm.minY) {
						mm.minY = y;
					}
					if (x > maxX) {
						maxX = x;
					}
					if (y > maxY) {
						maxY = y;
					}
				}
			}
		}
		if (mm.minX == Integer.MAX_VALUE) {
			mm.minX = 0;
		}
		if (mm.minY == Integer.MAX_VALUE) {
			mm.minY = 0;
		}
		mm.width = (maxX - mm.minX) + 1;
		mm.height = (maxY - mm.minY) + 1;

		return mm;
	}

	protected void setData(int[][] dataField) {
		data = dataField;
		settings.setIconWidth(getWidth());
		settings.setIconHeight(getHeight());
		notifyDataListeners(new ChangeEvent(this, CLIP_DATA_CHANGED));
	}

	public void dataChanged(ChangeEvent e) {
		switch (e.changeType) {
			case PALETTE_COLOUR_CHANGED :
				notifyDataListeners(new ChangeEvent(this, FRAME_NEEDS_REPAINT));
				break;
			case PALETTE_COLOUR_ADDED :
			case PICTURE_FOREGROUND_COLOUR_CHANGED :
			case PICTURE_BACKGROUND_COLOUR_CHANGED :
				// do nothing
				break;
			case PALETTE_CHANGED :
				notifyDataListeners(new ChangeEvent(this, FRAME_USES_NEW_PALETTE));
				break;
			default :
				Assert.fail("cannot handle " + e.toString());
		}
	}

	public static final class MinMax {
		public int minX = Integer.MAX_VALUE;
		public int minY = Integer.MAX_VALUE;
		public int width = 0;
		public int height = 0;

		public String toString() {
			return "minX: "
				+ minX
				+ ", minY: "
				+ minY
				+ ", width: "
				+ width
				+ ", height: "
				+ height;
		}
	}

}
