package jdraw.gio;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import jdraw.data.Frame;
import jdraw.data.FrameSettings;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.MainFrame;
import util.Assert;
import util.Log;
import util.SimpleLogListener;
import util.Util;

/*
 * GIFWriter.java - created on 28.10.2003
 * 
 * @author Michaela Behling
 */

public final class GIFWriter {

	private static final boolean DEBUG_WRITE = false;
	private static final int DEBUG_LEVEL = 1;
	private static final int MAX_CODE = 0xFFF;

	private static final int[] LOOP_Y = { 0, 4, 2, 1 };
	private static final int[] LOOP_OFFSET = { 8, 8, 4, 2 };

	private static final int BIT_0 = 1;
	private static final int BIT_1 = 2;
	private static final int BIT_2 = 4;
	private static final int BIT_3 = 8;
	private static final int BIT_4 = 16;
	private static final int BIT_5 = 32;
	private static final int BIT_6 = 64;
	private static final int BIT_7 = 128;

	private final Picture picture;
	private final OutputStream out;
	private int paletteSize;
	private Frame frame;

	private int initialCodeSize;
	private int codeSize;
	private final HashMap codeMap = new HashMap(4096);
	private int clear;
	private int endLZW;
	private int freeCode;
	private int lastCode;
	private StringBuffer output;
	private int currentData;
	private int availableBits;
	private final boolean saveInterlaced;
	private int loop;

	private int x;
	private int y;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int w;
	private int h;

	private GIFWriter(Picture aPicture, OutputStream o, boolean interlaced) {
		picture = aPicture;
		out = o;
		saveInterlaced = interlaced;
	}

	private static void debug(int level, String text) {
		if (level >= DEBUG_LEVEL) {
			Log.debug(text);
		}
	}

	public static boolean writeGIF(
		Picture pic,
		String fileName,
		boolean isInterlaced) {
		OutputStream o = null;
		try {
			o =
				new BufferedOutputStream(new FileOutputStream(fileName));
			GIFWriter writer = new GIFWriter(pic, o, isInterlaced);
			writer.writeGIF();
			return true;
		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
		finally {
			Util.close(o);
		}
	}

	public static boolean writeGIF(Picture pic, String fileName) {
		return writeGIF(pic, fileName, false);
	}


	public static boolean writeGIF(
		Picture pic,
		OutputStream o,
		boolean isInterlaced) {
		try {
			GIFWriter writer = new GIFWriter(pic, o, isInterlaced);
			writer.writeGIF();
			return true;
		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
		finally {						
			Util.close(o);
		}
	}

	public static boolean writeGIF(Picture pic, OutputStream o) {
		return writeGIF(pic, o, false);
	}

	private void writeGIF() throws Exception {
		try {
			writeSignature();
			boolean useGlobalPalette = writeScreenDescriptor();
			if (useGlobalPalette) {
				writePalette(picture.getPalette());
			}
			// image/extension blocks
			final int frameCount = picture.getFrameCount();
			if (frameCount > 1) {
				writeAnimationLoopExtension();
			}
			for (int i = 0; i < frameCount; i++) {
				writeFrame((Frame) picture.getFrame(i));
			}
			out.write(0x3b); // trailer
		}
		finally { // resource schliessen
			// out.flush(); ?
			Util.close(out);
		}
	}

	private final void writeAnimationLoopExtension() throws Exception {
		out.write(0x21); // extension introducer
		out.write(0xff); // extension type

		out.write(11); // first block length
		out.write("NETSCAPE".getBytes());
		out.write("2.0".getBytes());

		out.write(3); // second block length
		out.write(1); // kennung loop block
		writeInt(0);

		out.write(0); // block terminator
	}

	private void writeFrame(Frame aFrame) throws Exception {
		if (aFrame.usesTransparency() || (picture.getFrameCount() > 1)) {
			writeGraphicControlExtension(aFrame);
		}
		boolean useLocalPalette = writeImageDescriptor(aFrame);
		if (useLocalPalette) {
			writePalette(aFrame.getPalette());
		}
		writeImageData(aFrame);
	}

	private void addCode(int code, String value) {
		if (DEBUG_WRITE)
			debug(0, "CODE TABLE ENTRY: " + code + " --> " + Util.asBytes(value));
		codeMap.put(value, new Integer(code));
	}

	private void addCode(String value) {
		addCode(freeCode, value);
		if (freeCode == MAX_CODE) {
			if (DEBUG_WRITE)
				debug(0, "MAX CODE REACHED - RESET");
			output(clear);
			initCodes();
		}
		else {
			if (freeCode > lastCode) {
				increaseCodeSize();
			}
			freeCode++;
		}
	}

	private void initCodes() {
		codeSize = initialCodeSize + 1;
		if (DEBUG_WRITE)
			debug(0, "\ncodeSize = " + codeSize);
		final int max = (int) Math.pow(2, initialCodeSize) - 1;

		clear = max + 1;
		endLZW = max + 2;
		freeCode = max + 3;
		lastCode = (int) (Math.pow(2, codeSize) - 1);

		String value;
		codeMap.clear();

		for (int i = 0; i <= max; i++) {
			value = "" + ((char) i);
			addCode(i, value);
		}

		if (DEBUG_WRITE) {
			debug(
				0,
				"clear = " + Util.binaryString(clear, codeSize) + ", " + clear);
			debug(
				0,
				"lzw   = " + Util.binaryString(endLZW, codeSize) + ", " + endLZW);
			debug(
				0,
				"lastcode = "
					+ Util.binaryString(lastCode, codeSize)
					+ ", "
					+ lastCode);
			debug(
				0,
				"freecode ="
					+ freeCode
					+ " "
					+ Util.binaryString(freeCode, codeSize));
		}
	}

	private boolean dataAvailable() {
		if (DEBUG_WRITE)
			debug(
				0,
				"\ny = " + y + " (" + maxY + "), x = " + x + " (" + minX + ")");
		if (saveInterlaced) {
			return y != -1;
		}
		return (y != maxY + 1) || (x != minX);
	}

	private void compress(Frame aFrame) throws Exception {
		frame = aFrame;
		initCodes();
		availableBits = 0;
		currentData = 0;
		loop = 0;
		x = minX;
		y = minY;

		output = new StringBuffer();

		// begin output
		String prefix = "";
		char k;
		String current;
		output(clear);
		do {
			k = (char) getPixel();
			current = prefix + k;
			if (DEBUG_WRITE)
				debug(0, "\nlooking for " + Util.asBytes(current));

			int code = findString(current);
			if (code != -1) { // bekannter string
				prefix = current;
				if (DEBUG_WRITE)
					debug(0, "extending prefix");
			}
			else { // neuer string
				output(prefix);
				addCode(current);
				prefix = "" + k;
			}
		}
		while (dataAvailable());

		if (prefix.length() > 0) {
			if (DEBUG_WRITE)
				debug(0, "outputting prefix " + findString(prefix));
			output(findString(prefix));
		}
		if (DEBUG_WRITE) {
			debug(0, "availableBits: " + availableBits);
			debug(0, "appending endlzw = " + Util.binaryString(endLZW, codeSize));
		}
		output(endLZW);

		if (DEBUG_WRITE)
			debug(
				0,
				"available bits = "
					+ Util.binaryString(currentData, availableBits));
		while (availableBits > 0) { // flush
			int b = 0xFF & currentData;
			if (DEBUG_WRITE)
				debug(0, "flushing " + Util.hexString(b, 2));
			output.append((char) b);
			currentData = currentData >> 8;
			availableBits = availableBits - 8;
		}

		packData();
	}

	private void packData() throws Exception {
		String sub;
		int len = output.length();
		int pos = 0;
		while (len > 0) {
			int blockLen = Math.min(255, len);
			sub = output.substring(pos, pos + blockLen);
			out.write(blockLen);

			//	!!! output.getbytes returned wrong values
			for (int i = 0; i < blockLen; i++) {
				out.write(sub.charAt(i));
			}
			pos = pos + blockLen;
			len = len - (blockLen);

			if (DEBUG_WRITE)
				debug(0, "image block written.");
		}
	}

	private void increaseCodeSize() {

		codeSize = codeSize + 1;
		lastCode = (int) (Math.pow(2, codeSize) - 1);

		if (DEBUG_WRITE) {
			debug(0, "\nincreased codeSize = " + codeSize);
			debug(
				0,
				"lastcode = "
					+ Util.binaryString(lastCode, codeSize)
					+ ", "
					+ lastCode);
			debug(
				0,
				"freecode ="
					+ freeCode
					+ " "
					+ Util.binaryString(freeCode, codeSize));
		}
	}

	private int findString(String s) {
		Integer code = (Integer) codeMap.get(s);
		if (code == null) {
			return -1;
		}
		return code.intValue();
	}

	private int getPixel() {
		int p = frame.getPixel(x, y);
		if (DEBUG_WRITE)
			debug(1, "seeing pixel [" + x + "," + y + "] = " + p);
		x++;
		if (x > maxX) {
			x = minX;
			if (saveInterlaced) {
				y = y + LOOP_OFFSET[loop];
				while (y >= minY + h) { // naechster loop
					if (DEBUG_WRITE)
						debug(1, ">>> interlace loop " + (loop + 1));
					loop++;
					if (loop < LOOP_Y.length) {
						y = minY + LOOP_Y[loop];
					}
					else {
						y = -1;
						if (DEBUG_WRITE) {
							debug(1, "end of interlaced data reached");
						}
						break;
					}
				}
			}
			else {
				y++;
			}
			if (DEBUG_WRITE)
				debug(1, "y=" + y);
		}
		return p;
	}

	private void output(String s) {
		int code = findString(s);
		Assert.isTrue(
			code != -1,
			"gio: internal error. code 0x" + Util.asBytes(s) + " not found.");
		output(code);
	}

	private void output(int b) {
		if (DEBUG_WRITE)
			debug(0, "----> output  " + Util.binaryString(b, codeSize));

		b = b << availableBits;
		currentData = currentData | b;
		availableBits = availableBits + codeSize;

		while (availableBits >= 8) { // ein byte kann geschrieben werden			
			int outByte = 0xFF & currentData;
			output.append((char) outByte);
			currentData = currentData >> 8;
			availableBits = availableBits - 8;
		}
	}

	private void writeImageData(Frame aFrame) throws Exception {
		initialCodeSize = paletteSize + 1;
		out.write(initialCodeSize);
		if (DEBUG_WRITE)
			debug(0, "initial code size = " + initialCodeSize);

		compress(aFrame);

		out.write(0); // block terminator
	}

	private boolean writeImageDescriptor(Frame aFrame) throws Exception {
		out.write(0x2c); // image separator

		Frame.MinMax mm = aFrame.getDimensions();

		minX = mm.minX;
		minY = mm.minY;
		w = mm.width;
		h = mm.height;
		maxX = (mm.minX + w) - 1;
		maxY = (mm.minY + h) - 1;
		writeInt(minX);
		writeInt(minY);
		writeInt(w);
		writeInt(h);

		int b = 0; // packed field

		boolean useLocalPalette =
			!aFrame.getPalette().equals(picture.getPalette());
		if (useLocalPalette) {
			b = b | BIT_7;
		}
		boolean interlaced = saveInterlaced;
		if (interlaced) {
			b = b | BIT_6;
		}

		if (useLocalPalette) {
			paletteSize = getPaletteSize(aFrame.getPalette());
			if (DEBUG_WRITE)
				debug(0, "paletteSize = " + paletteSize + " - written");
			b = b + paletteSize;
		}
		out.write(b);

		if (DEBUG_WRITE)
			debug(0, "image descriptor written.");
		return useLocalPalette;
	}

	private final int getPaletteSize(Palette pal) {
		final int size = pal.size();
		int exp = 1;
		while (Math.pow(2, exp) < size) {
			exp++;
		}
		return Math.max(1, exp - 1);
	}

	private void writeGraphicControlExtension(Frame aFrame) throws Exception {
		out.write(0x21); // extension introducer
		out.write(0xf9); // extension type
		out.write(4); // block length

		// packed byte
		FrameSettings settings = aFrame.getSettings();
		int b = 0;
		if (aFrame.usesTransparency()) {
			b = b | BIT_0;
		}
		b = b | (settings.getDisposalMethod() << 2);
		out.write(b);

		writeInt(settings.getDelay());

		b = 0;
		if (aFrame.usesTransparency()) {
			b = aFrame.getTransparentColour();
		}
		out.write(b);

		out.write(0); // block terminator
		if (DEBUG_WRITE)
			debug(0, "graphic control extension written.");
	}

	private void writeSignature() throws Exception {
		out.write("GIF89a".getBytes());
	}

	private final void writePalette(Palette pal) throws Exception {
		int size = pal.size();
		Color col;
		for (int i = 0; i < size; i++) {
			col = pal.getColour(i).getColour();
			out.write(col.getRed());
			out.write(col.getGreen());
			out.write(col.getBlue());
		}
		int maxSize = (int) Math.pow(2, paletteSize + 1);
		final int missing = maxSize - size;
		if (missing > 0) {
			byte[] black = new byte[3];
			byte zero = 0;
			Arrays.fill(black, 0, 3, zero);
			for (int i = 0; i < missing; i++) {
				out.write(black);
			}
		}
		if (DEBUG_WRITE)
			debug(0, "palette with " + (size + missing) + " colours written.");
	}

	private boolean writeScreenDescriptor() throws Exception {
		writeInt(picture.getWidth());
		writeInt(picture.getHeight());
		// packed field
		int b = 0;

		boolean useGlobalPalette = picture.usesGlobalPalette();
		if (useGlobalPalette) {
			b = b | BIT_7;
		}
		paletteSize = getPaletteSize(picture.getPalette());
		int cr = paletteSize;
		b = b | (cr << 4);
		if (DEBUG_WRITE)
			debug(0, "paletteSize = " + paletteSize);
		b = b + paletteSize;
		out.write(b);
		out.write(picture.getPictureBackground());
		int ratio = 0;
		out.write(ratio);
		if (DEBUG_WRITE)
			debug(0, "screen descriptor written.");
		return useGlobalPalette;
	}

	private void writeInt(int i) throws Exception {
		out.write(i);
		out.write(i >> 8);
	}

	public static void main(String[] args) {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Picture pic = GIFReader.readGIF("/home/michaela/tmp/test.gif");
		Log.addLogListener(listener);
		if (pic != null) {
			GIFWriter.writeGIF(pic, "/home/michaela/tmp/out.gif");
		}
	}
}
