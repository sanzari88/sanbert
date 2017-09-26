package jdraw.gio;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import jdraw.data.ColourEntry;
import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Assert;
import util.Log;
import util.SimpleLogListener;
import util.Util;

/*
 * IconWriter.java - created on 9.12.2003
 * 
 * @author Michaela Behling
 */

public final class IconWriter {

	private static final int ICON_HEADER_SIZE = 40;
	private static final int FILE_HEADER_SIZE = 6;
	private static final int DICTIONARY_ENTRY_SIZE = 16;

	private final OutputStream out;

	private final Picture picture;
	private int bytesWritten = 0;
	private final ArrayList icons = new ArrayList();

	private IconWriter(OutputStream stream, Picture aPicture) {
		out = stream;
		picture = aPicture;
	}

	public static boolean writeIcon(Picture pic, String fileName) {
		try {			
			IconWriter writer =
				new IconWriter(new FileOutputStream(fileName), pic);
			writer.writeIcon();
			return true;

		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	private void writeIcon() throws Exception {
		writeHeader();
		writeIconEntries();
		writeIconData();
		// aufräumen
		icons.clear();
	}

	private void writeHeader() throws Exception {
		writeInt(0); // reserved (always 0)
		writeInt(1); // resource id (always 1)
		writeInt(picture.getFrameCount());
	}

	private void writeIconEntries() throws Exception {
		final int frameCount = picture.getFrameCount();
		IconEntry e;
		for (int i = 0; i < frameCount; i++) {
			e = new IconEntry(picture.getFrame(i));
			icons.add(e);
		}

		int offset = FILE_HEADER_SIZE + (frameCount * DICTIONARY_ENTRY_SIZE);
		for (int i = 0; i < frameCount; i++) {
			e = (IconEntry) icons.get(i);
			e.writeDictionaryEntry(offset);
			offset = offset + e.getSize();
		}
	}

	private void writeIconData() throws Exception {
		final int frameCount = picture.getFrameCount();
		IconEntry e;
		for (int i = 0; i < frameCount; i++) {
			e = (IconEntry) icons.get(i);
			e.writeData();
		}
	}

	private void writeInt(int i) throws Exception {
		out.write(i);
		out.write(i >> 8);
		bytesWritten = bytesWritten + 2;
	}

	private void writeByte(int b) throws Exception {
		out.write(b);
		bytesWritten++;
	}

	private void writeByte(byte[] data) throws Exception {
		bytesWritten = bytesWritten + data.length;
		out.write(data);
	}

	private void writeDWord(int i) throws Exception {
		int a = (i & 0xFFFF);
		int b = (i >> 16) & 0xFFFF;
		writeInt(a);
		writeInt(b);
	}

	public static void main(String[] args) throws Exception {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Log.addLogListener(listener);
		Picture pic = IconReader.readIcon("C:/Temp/test.ico");
		IconWriter.writeIcon(pic, "C:/Temp/testout.ico");
	}

	private final class IconEntry {
		public final Palette pal;
		public final Frame frame;
		public final int width;
		public final int height;
		public final int numColours;
		public final int reserved = 0;
		public final int numPlanes = 1;
		public final int bitsPerPixel;
		public final int dataSize;
		public final byte[] bitMap;
		public final byte[] andMap;
		private int offset = 0;

		public final ArrayList colours = new ArrayList();

		public IconEntry(Frame aFrame) throws Exception {
			frame = aFrame;
			pal = frame.getPalette();
			width = frame.getSettings().getIconWidth();
			height = frame.getSettings().getIconHeight();
			int palSize = frame.getPalette().size();
			if (frame.getTransparentColour() != -1) {
				palSize--;
			}
			int div = 0;
			if (palSize <= 2) {
				bitsPerPixel = 1;
				div = 8;
			}
			else if (palSize <= 16) {
				bitsPerPixel = 4;
				div = 2;
			}
			else {
				bitsPerPixel = 8;
				div = 1;
			}
			numColours = 1 << bitsPerPixel;
			Assert.isTrue(div != 0, "gio: internal error. div = 0");
			int xorMapLen = (width * height) / div;
			if (xorMapLen % div != 0) {
				xorMapLen++;
			}
			bitMap = new byte[xorMapLen];
			int andMapLen = (width * height) / 8;
			if (andMapLen % 8 != 0) {
				andMapLen++;
			}
			andMap = new byte[andMapLen];

			dataSize = getSize();
			createData();

			if (Log.DEBUG) {
				Log.debug("IconEntry");
				Log.debug("   width:   " + width);
				Log.debug("   height:  " + height);
				Log.debug("   colours: " + numColours);
				Log.debug("   size:    " + dataSize);
			}
		}

		private void writeDictionaryEntry(int dataOffset) throws Exception {
			offset = dataOffset;
			writeByte(width);
			writeByte(height);
			writeByte(numColours);
			writeByte(reserved);
			writeInt(numPlanes);
			writeInt(bitsPerPixel);
			writeDWord(dataSize);
			writeDWord(dataOffset);
		}

		public void writeData() throws Exception {
			Assert.isTrue(
				offset == bytesWritten,
				"gio: wrong position."
					+ "bytesWritten="
					+ bytesWritten
					+ ", offset="
					+ offset);

			writeDWord(ICON_HEADER_SIZE); // size of this header in bytes
			writeDWord(width); // image width in pixels
			writeDWord(height * 2); // Image height in pixels
			writeInt(numPlanes); // number of color planes
			writeInt(bitsPerPixel); // number of bits per pixel
			writeDWord(0); // compression methods used
			writeDWord(bitMap.length); // size of bitmap in bytes
			writeDWord(0); // horizontal resolution in pixels per meter
			writeDWord(0); // vertical resolution in pixels per meter
			writeDWord(numColours); // number of colors in the image
			writeDWord(numColours); // minimum number of important colours

			if (Log.DEBUG) {
				Log.debug("\nIcon Data");
				Log.debug("   bitsPerPixel: " + bitsPerPixel);
				Log.debug("   width:        " + width);
				Log.debug("   height:       " + (height * 2));
				Log.debug("   bitmapSize:   " + bitMap.length);
				Log.debug("   colourCount:  " + numColours);
			}
			
			Color col;
			final int transIndex = frame.getTransparentColour();
			final boolean hasTransColour = (transIndex != -1);
			final int size = pal.size();
			final int colourCount = hasTransColour? numColours+1 : numColours;			
			for (int i = 0; i < colourCount; i++) {
				if (i != transIndex) {
					if (i < size) {
						col = pal.getColour(i).getColour();
					}
					else {
						col = Color.black;
					}
					writeByte(col.getBlue());
					writeByte(col.getGreen());
					writeByte(col.getRed());
					writeByte(0); // filler
				}
			}
			writeByte(bitMap);
			writeByte(andMap);
		}

		public void createData() {
			createAndMask();
			switch (bitsPerPixel) {
				case 1 :
					// bild bereits durch andMask definiert				
					Arrays.fill(bitMap, (byte) 1);
					break;
				case 4 :
					createData4();
					break;
				case 8 :
					createData8();
					break;
				default :
					Assert.fail(
						"gio: cannot handle icons with bitsPerPixel=" + bitsPerPixel);
			}
		}

		public int getSize() {
			return ICON_HEADER_SIZE
				+ bitMap.length
				+ andMap.length
				+ (numColours * 4);
		}

		private void createAndMask() {
			int index = 0;
			int bits = 0;
			int b = 0;
			int pixel;
			final int trans = frame.getTransparentColour();
			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					pixel = frame.getPixel(x, y);
					if (pixel == trans) { // hide bitmap: bit = 1
						b = b + 1;
					}
					bits++;
					if (bits == 8) {
						andMap[index] = (byte) b;
						b = 0;
						index++;
						bits = 0;
					}
					else {
						b = b << 1;
					}
				}
			} // end for y
			if (bits > 0) {
				andMap[++index] = (byte) b;
			}
		}

		private void createData4() {
			int index = 0;
			int bits = 0;
			int b = 0;
			int pixel;
			final int transIndex = frame.getTransparentColour();
			final boolean hasTransColour = (transIndex != -1);

			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					pixel = frame.getPixel(x, y);
					if (hasTransColour && (pixel > transIndex)) {
						pixel = pixel-1;
					}
					b = b + pixel;
					bits = bits + 4;
					if (bits == 8) {
						bitMap[index] = (byte) b;
						b = 0;
						index++;
						bits = 0;
					}
					else {
						b = b << 4;
					}
				}
			} // end for y
			if (bits > 0) {
				bitMap[++index] = (byte) b;
			}
		}

		private void createData8() {
			int index = 0;
			final int transIndex = frame.getTransparentColour();
			final boolean hasTransColour = (transIndex != -1);

			int pixel;
			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					pixel = frame.getPixel(x, y);
					if (hasTransColour && (pixel > transIndex)) {
						pixel = pixel-1;
					}
					bitMap[index] = (byte) pixel;
					index++;
				}
			}
		}
	}

}
