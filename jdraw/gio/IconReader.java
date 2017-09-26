package jdraw.gio;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Assert;
import util.Log;
import util.SimpleLogListener;

/*
 * IconReader.java - created on 5.12.2003
 * 
 * @author Michaela Behling
 */

public final class IconReader {

	private final InputStream in;

	private Picture picture;
	private int bytesRead = 0;

	private int[] dataLengths;

	private int iconCount;
	private final ArrayList icons = new ArrayList();

	private IconReader(InputStream stream) {
		in = stream;
	}

	public static Picture readIcon(String fileName) {
		try {
			IconReader reader = new IconReader(new FileInputStream(fileName));
			Picture pic = reader.readIcon();
			Palette palette = pic.getPalette();
			while (palette.size() < 2) {
				palette.addColour(Color.white);
			}
			pic.setTransparent(0);
			pic.show();
			return pic;

		}
		catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	private Picture readIcon() throws Exception {
		readHeader();
		readIconEntries();
		readIconData();

		Dimension dim = getMaxDimension();
		picture = new Picture(dim.width, dim.height);

		Iterator it = icons.iterator();
		IconEntry e;
		Frame frame;
		while (it.hasNext()) {
			e = (IconEntry) it.next();
			try {

				frame = e.createFrame();
				picture.addFrame(frame);
				if (picture.getPalette() == null) {
					picture.setCurrentFrame(0);
					picture.setPalette(frame.getPalette());					
				}
			}
			catch (Exception ex) {
				Log.exception(ex);
			}
		}

		if (Log.DEBUG && (readByte() != -1)) {
			Log.debug("Ignoring extra data...");
		}
		return picture;
	}

	private Dimension getMaxDimension() {
		Iterator it = icons.iterator();
		IconEntry e;
		int w = 0;
		int h = 0;
		while (it.hasNext()) {
			e = (IconEntry) it.next();
			if (e.width > w) {
				w = e.width;
			}
			if (e.height > h) {
				h = e.height;
			}
		}
		return new Dimension(w, h);
	}

	private void readIconData() throws Exception {
		IconEntry e;
		for (int i = 0; i < iconCount; i++) {
			e = (IconEntry) icons.get(i);
			e.readData();
		}
	}

	private void readIconEntries() throws Exception {
		for (int i = 0; i < iconCount; i++) {
			icons.add(new IconEntry());
		}
	}

	private void readHeader() throws Exception {
		int i = 0;
		i = readInt(); // reserved (always 0)
		Assert.isTrue(i == 0, "gio: expected 0, got " + i);
		i = readInt(); // resource ID (always 1)
		Assert.isTrue(i == 1, "gio: expected id 1, got " + i);
		iconCount = readInt();
		if (Log.DEBUG) {
			Log.debug("-- stored icons: " + iconCount);
		}
	}

	// 1 bit entspricht einem pixel
	private int[] getPixels1(byte[] data, boolean[] flags) {
		final int len = data.length;
		int[] pixel = new int[len * 8];

		int b;
		int index = 0;
		for (int i = 0; i < len; i++) {
			b = data[i];
			for (int j = 0; j < 8; j++) {
				if (flags[index]) {
					pixel[index] = ((b >> (7 - j)) & 1) + 1;
				}
				else {
					pixel[index] = 0;
				}
				index++;
			}
		}
		return pixel;
	}

	private int[] getPixels4(byte[] data, boolean[] flags) {
		final int len = data.length;
		int[] pixel = new int[len * 2];

		int x1, x2;
		int b;
		int index = 0;
		for (int i = 0; i < len; i++) {
			b = data[i];
			x1 = 0x0f & (b >> 4);
			x2 = 0x0f & (b);
			if (flags[index]) {
				pixel[index] = x1 + 1;
			}
			index++;
			if (flags[index]) {
				pixel[index] = x2 + 1;
			}
			index++;
		}
		return pixel;
	}

	private int[] getPixels8(byte[] data, boolean[] flags) {
		final int len = data.length;
		int[] pixel = new int[len];

		int index = 0;
		for (int i = 0; i < len; i++) {
			if (flags[index]) {
				pixel[index] = (data[i] & 0xff)+ 1;
			}
			else {
				pixel[index] = 0;
			}
			index++;
		}
		return pixel;
	}

	private boolean[] createBooleanField(byte[] andMask) {
		final int len = andMask.length;
		boolean[] flags = new boolean[len * 8];
		int b;
		int index = 0;
		for (int i = 0; i < len; i++) {
			b = andMask[i];
			for (int j = 0; j < 8; j++) {
				//	true  == colour pixel
				// false == transparent pixel
				flags[index] = ((b >> (7 - j)) & 1) == 0;
				index++;
			}
		}
		return flags;
	}

	private int readInt() throws Exception {
		int a = readByte();
		int b = readByte();
		b = (b << 8);

		return a + b;
	}

	private int readByte() throws Exception {
		bytesRead++;
		int b = in.read();
		return b;
	}

	private void readByte(byte[] data) throws Exception {
		bytesRead = bytesRead + data.length;
		in.read(data);
	}

	private int readDWord() throws Exception {
		int a = readInt();
		int b = readInt();
		b = b << 16;
		return a + b;
	}

	public static void main(String[] args) {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Log.addLogListener(listener);
		readIcon("C:/Temp/test.ico");
	}

	private final class IconEntry {
		public int width;
		public int height;
		public int numColours;
		public int reserved;
		public int numPlanes;
		public int bitsPerPixel;
		public int dataSize;
		public int dataOffset;
		public byte[] bitMap;
		public byte[] andMap;

		public final ArrayList colours = new ArrayList();

		public IconEntry() throws Exception {
			width = readByte();
			height = readByte();
			numColours = readByte();
			reserved = readByte();
			numPlanes = readInt();
			bitsPerPixel = readInt();
			dataSize = readDWord();
			dataOffset = readDWord();

			if (Log.DEBUG) {
				Log.debug("IconEntry");
				Log.debug("   width:   " + width);
				Log.debug("   height:  " + height);
				Log.debug("   colours: " + numColours);
				Log.debug("   size:    " + dataSize);
				Log.debug("   offset:  " + dataOffset);
			}
		}

		public void readData() throws Exception {
			while (bytesRead < dataOffset) {
				Log.debug("swallowing a byte...");
				readByte();
			}
			int i = 0;

			i = readDWord(); // size of this header in bytes
			int w = readDWord(); // image width in pixels
			int h = readDWord(); // Image height in pixels
			i = readInt(); // number of color planes
			bitsPerPixel = readInt(); // number of bits per pixel
			i = readDWord(); // compression methods used
			int bitmapSize = readDWord(); // size of bitmap in bytes
			i = readDWord(); // horizontal resolution in pixels per meter
			i = readDWord(); // vertical resolution in pixels per meter
			int colourCount = readDWord(); // number of colors in the image
			if (colourCount == 0) {
				colourCount = 1 << bitsPerPixel;
			}
			i = readDWord(); // minimum number of important colours

			if (Log.DEBUG) {
				Log.debug("\nIcon Data");
				Log.debug("   bitsPerPixel: " + bitsPerPixel);
				Log.debug("   width:        " + w);
				Log.debug("   height:       " + h);
				Log.debug("   bitmapSize:   " + bitmapSize);
				Log.debug("   colourCount:  " + colourCount);
			}

			final boolean trueColour = (bitsPerPixel > 8);
			if (!trueColour) {
				int r, g, b;
				for (i = 0; i < colourCount; i++) {
					b = readByte();
					g = readByte();
					r = readByte();
					readByte(); // reserved					
					colours.add(new Color(r, g, b));
				}
				//	xor-map	
				int bitMapLen = (width * height);
				if (bitsPerPixel == 1) {
					bitMapLen = (bitMapLen / 8);
					if (bitMapLen % 8 > 0) {
						bitMapLen++;
					}
				}
				else if (bitsPerPixel == 4) {
					bitMapLen = (bitMapLen / 2);
					if (bitMapLen % 2 > 0) {
						bitMapLen++;
					}
				} // else: bitsPerPixel = 8 und bitMapLen ist OK				
				bitMap = new byte[bitMapLen];
				readByte(bitMap);
				// and-map	
				int andMapLen = (width * height) / 8;
				if (andMapLen % 8 > 0) {
					andMapLen++;
				}
				andMap = new byte[andMapLen];
				readByte(andMap);
			}
			else {
				throw new RuntimeException("True colour icons not supported!");
			}
		}

		public int[] getPixels() {
			boolean[] andFlags = createBooleanField(andMap);

			switch (bitsPerPixel) {
				case 1 :
					return getPixels1(bitMap, andFlags);
				case 4 :
					return getPixels4(bitMap, andFlags);
				case 8 :
					return getPixels8(bitMap, andFlags);
				default :
					Assert.fail(
						"gio: cannot handle icons with bitsPerPixel=" + bitsPerPixel);
					return null;
			}
		}

		public Frame createFrame() {
			Frame f = new Frame(picture);
			f.getSettings().setIconWidth(width);
			f.getSettings().setIconHeight(height);
			Palette palette = new Palette(picture);
			Iterator it = colours.iterator();
			palette.addColour(Color.black);
			f.setTransparent(0);			
			while (it.hasNext()) {
				palette.addColour((Color) it.next());
			}
			f.setPalette(palette);			
			int[] pixel = getPixels();
			int index = 0;
			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					f.setPixelQuiet(x, y, pixel[index]);
					index++;
				}
			}
			colours.clear();
			pixel = null;
			bitMap = null;
			andMap = null;
			return f;
		}
	}

}
