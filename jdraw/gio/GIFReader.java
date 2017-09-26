package jdraw.gio;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import jdraw.data.Frame;
import jdraw.data.FrameSettings;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Assert;
import util.Log;
import util.SimpleLogListener;
import util.Util;

/*
 * GIFReader.java - created on 27.10.2003
 * 
 * @author Michaela Behling
 */

public final class GIFReader {

	private static final boolean DEBUG_READ = false;
	private static final int DEBUG_LEVEL = 1;
	private static final int MAX_CODE_SIZE = 12;

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

	private final InputStream in;

	private Picture picture;
	private int currentFrame = -1;

	private GraphicControlExtension gce = null;
	private int paletteSize;
	private int minX;
	private int minY;
	private int w;
	private int h;
	private boolean interlaced;
	private int bytesAvailable;

	private int clear;
	private int endLZW;

	private int initialcodeSize;
	private int codeSize;
	private final HashMap codeMap = new HashMap(4096);
	private int freeCode;
	private int lastCode;
	private int code;
	private int currentData;
	private int availableBits;
	private int x;
	private int y;
	private int loop;

	private GIFReader(InputStream input) {
		in = input;
	}

	private static void debug(int level, String text) {
		if (level >= DEBUG_LEVEL) {
			Log.debug(text);
		}
	}

	public static final Picture readGIF(String fileName) {
		try {
			InputStream input =
				new BufferedInputStream(new FileInputStream(fileName));
			GIFReader reader = new GIFReader(input);
			return reader.readGIF();
		}
		catch (FileNotFoundException fe) {
			Log.error("File '" + fileName + "' not found.");
			return null;
		}
		catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	public static final Picture readGIF(InputStream input) {
		try {
			GIFReader reader = new GIFReader(input);
			return reader.readGIF();
		}
		catch (Exception e) {
			Log.exception(e);
			return null;
		}
	}

	private int readInt() throws Exception {
		int a = in.read();
		int b = in.read();
		b = (b << 8);

		return a + b;
	}

	private Picture readGIF() throws Exception {
		try {
			readSignature();
			boolean globalMapFollows = readScreenDescriptor();
			if (globalMapFollows) {
				picture.setPalette(readPalette());
			}
			else {
				picture.setPalette(new Palette(picture));
			}

			char separator;
			do {
				separator = (char) in.read();
				switch (separator) {
					case ',' :
						boolean localPaletteFollows = readImageDescriptor();
						if (localPaletteFollows) {
							Palette pal = readPalette();
							picture.getFrame(currentFrame).setPalette(pal);
						}
						readImageData();
						break;
					case '!' :
						readExtensionBlock();
						break;
					case ';' :
						break;
					case 0x00 :
						Log.info("Ignoring unexpected block separator...");
						break;
					default :
						Assert.fail(
							"gio: unknown separator '"
								+ separator
								+ "' ("
								+ Util.hexString(separator, 2)
								+ ")");
				}
			}
			while (separator != ';');
			return picture;
		}
		finally {
			Util.close(in);
		}
	}

	private void initCodes() {
		codeSize = initialcodeSize + 1;
		if (DEBUG_READ)
			debug(0, "\ncodeSize = " + codeSize);
		final int max = (int) Math.pow(2, initialcodeSize) - 1;

		Integer aCode;
		String value;

		codeMap.clear();
		for (int i = 0; i <= max; i++) {
			aCode = new Integer(i);
			value = "" + ((char) i);
			codeMap.put(aCode, value);
		}
		clear = max + 1;
		endLZW = max + 2;
		freeCode = max + 3;
		lastCode = (int) (Math.pow(2, codeSize) - 1);

		if (DEBUG_READ) {
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
			debug(0, "freecode =" + freeCode);
		}
	}

	private void increaseCodeSize() {
		if (codeSize < MAX_CODE_SIZE) {
			codeSize = codeSize + 1;
			lastCode = (int) (Math.pow(2, codeSize) - 1);

			if (DEBUG_READ) {
				debug(0, "\nincreased codeSize = " + codeSize);
				debug(
					0,
					"lastcode = "
						+ Util.binaryString(lastCode, codeSize)
						+ ", "
						+ lastCode);
				debug(0, "freecode =" + freeCode);
			}
		}
		else {
			if (Log.DEBUG) {
				Log.debug(">> cannot increase code size");
			}
		}
	}

	private String findCode(int aCode) {
		return (String) codeMap.get(new Integer(aCode));
	}

	private void addCode(String codeString) {
		if (DEBUG_READ)
			debug(0, "-- adding " + Util.asBytes(codeString) + " as #" + freeCode);
		Assert.isTrue(
			(codeString != null) && (codeString.length() > 0),
			"gio: invalid code");

		codeMap.put(new Integer(freeCode), codeString);
		freeCode++;
		if (freeCode > lastCode) {
			increaseCodeSize();
		}
	}

	private void readImageData() throws Exception {
		initialcodeSize = in.read();
		if (DEBUG_READ)
			debug(0, "initial code size = " + initialcodeSize);
		initCodes();
		availableBits = 0;
		currentData = 0;
		loop = 0;
		x = minX;
		y = minY + LOOP_Y[loop];

		nextCode();
		Assert.isTrue(
			code == clear,
			"gio: clear expected instead of " + Util.binaryString(code, codeSize));
		if (DEBUG_READ)
			debug(0, "clear");
		nextCode();
		if (DEBUG_READ)
			debug(0, "--> Seeing " + Util.binaryString(code, codeSize));
		output(findCode(code));
		int previousCode = code;

		boolean endLZWSeen = false;
		do {
			nextCode();
			if (DEBUG_READ)
				debug(0, "--> Seeing " + Util.binaryString(code, codeSize));
			if (code == clear) {
				if (DEBUG_READ)
					debug(0, "CLEAR - INITIALIZING...");
				initCodes();
				nextCode();
				output(findCode(code));
				previousCode = code;
			}
			else if (code == endLZW) {
				if (DEBUG_READ)
					debug(0, "end lzw");
				endLZWSeen = true;
			}
			else { // daten code				
				String value = findCode(code);
				if (value != null) { // bekannter code					
					output(value);
					String prefix = findCode(previousCode);
					char k = value.charAt(0);
					addCode(prefix + k);
					prefix = prefix + k;

				}
				else { // neuer code										
					String prefix = findCode(previousCode);
					Assert.notNull(
						prefix,
						"gio: prefix is null. missing code #"
							+ previousCode
							+ ", "
							+ Util.binaryString(previousCode, codeSize));

					char k = prefix.charAt(0);
					output(prefix + k);
					addCode(prefix + k);
				}

				previousCode = code;
			}
		}
		while (!endLZWSeen);

		int b = in.read();
		// ende datenbloecke
		Assert.isTrue(
			b == 0,
			"gio: missing block terminator. found "
				+ Util.binaryString(b, codeSize)
				+ " instead.");

		if (DEBUG_READ)
			debug(0, "found image block");
	}

	private void output(String prefix) {
		final int len = prefix.length();
		if (len == 0)
			return;

		Frame frame = picture.getFrame(currentFrame);
		int pixel;
		for (int i = 0; i < len; i++) {
			pixel = (int) prefix.charAt(i);
			if (DEBUG_READ)
				debug(1, "----> outputting [" + x + "," + y + "] = " + pixel);

			Assert.isFalse(
				y >= minY + h,
				"gio: y out of range, " + y + " >= " + (minY + h));
			frame.setPixel(x, y, pixel);
			x++;
			if (x == (minX + w)) { // zeile beendet
				x = minX;
				// naechste zeile
				if (interlaced) {
					y = y + LOOP_OFFSET[loop];
					while (y >= minY + h) { // naechster loop
						if (DEBUG_READ)
							debug(1, ">>> interlace loop " + (loop + 1));
						loop++;
						if (loop < LOOP_Y.length) {
							y = minY + LOOP_Y[loop];
						}
						else {
							y = -1;
							if (DEBUG_READ) {
								debug(1, "end of interlaced data reached");
							}
							break;
						}
					}
				}
				else {
					y++;
				}
				if (DEBUG_READ)
					debug(1, "y=" + y);
			}
		}
	}

	private void nextCode() throws Exception {
		while (availableBits < codeSize) { // zuwenig daten
			if (bytesAvailable == 0) {
				bytesAvailable = in.read();
				Assert.isTrue(bytesAvailable > 0, "gio: out of data");
			}
			int b = in.read();
			bytesAvailable--;
			b = b << availableBits;
			currentData = currentData | b;
			availableBits = availableBits + 8;
		}

		// bits available >= codelen
		int mask = 1;
		for (int i = 1; i < codeSize; i++) {
			mask = (mask << 1) + 1;
		}
		code = currentData & mask;
		currentData = currentData >> codeSize;
		availableBits = availableBits - codeSize;
	}

	private void readExtensionBlock() throws Exception {
		int aCode = in.read();

		switch (aCode) {
			case 0xF9 :
				readGraphicControlExtension();
				break;
			default :
				Log.info(
					"gio: skipping extension block of type '"
						+ aCode
						+ "', 0x"
						+ Util.hexString(aCode, 2));
				int len;
				byte[] data = new byte[256];
				do {
					len = in.read();
					if (len > 0) {
						in.read(data, 0, len);
					}
				}
				while (len > 0);
		}
	}

	private void readGraphicControlExtension() throws Exception {
		gce = new GraphicControlExtension();

		in.read(); // block length
		int b = in.read();
		gce.useTransparentColour = (b & BIT_0) > 0;
		gce.disposalMethod = (b & (BIT_2 + BIT_3 + BIT_4)) >> 2;
		gce.delay = readInt();
		gce.transparentColour = in.read();

		b = in.read();
		Assert.isTrue(b == 0, "gio: missing extension block terminator");
		if (DEBUG_READ) {
			debug(0, "found graphic control extension");
		}
	}

	private boolean readImageDescriptor() throws Exception {
		minX = readInt();
		minY = readInt();
		w = readInt();
		h = readInt();

		currentFrame++;
		Frame frame = picture.addFrame();
		if (gce != null) {
			if (gce.useTransparentColour) {
				frame.setTransparentColour(gce.transparentColour);
			}
			FrameSettings settings = frame.getSettings();
			settings.setDelay(gce.delay);
			settings.setDisposalMethod(gce.disposalMethod);
			gce = null;
		}

		int b = in.read();
		boolean localPaletteFollows = (b & BIT_7) > 0;
		interlaced = (b & BIT_6) > 0;
		//		if (interlaced) {
		//			throw new RuntimeException("Cannot handle interlaced GIF.");
		//		}
		boolean sorted = (b & BIT_5) > 0;
		final int pixel = b & (BIT_2 + BIT_1 + BIT_0);
		paletteSize = (int) Math.pow(2, pixel + 1);

		if (DEBUG_READ) {
			debug(0, "\nminX = " + minX);
			debug(0, "minY = " + minY);
			debug(0, "w = " + w);
			debug(0, "h = " + h);
			debug(0, "sorted = " + sorted);
			debug(0, "interlaced = " + interlaced);
			debug(0, "paletteSize = " + paletteSize + " - read: " + pixel);
		}
		return localPaletteFollows;
	}

	private void readSignature() throws Exception {
		String gif89 = "GIF89a";
		String gif87 = "GIF87a";
		byte[] data = new byte[gif89.length()];
		in.read(data);
		String s = new String(data);
		Assert.isTrue(
			s.equals(gif89) || s.equals(gif87),
			"gio: unknown signature '" + s + "'");
	}

	private boolean readScreenDescriptor() throws Exception {
		int width = readInt();
		int height = readInt();

		picture = new Picture(width, height);

		int b = in.read();
		boolean globalMapFollows = (b & BIT_7) > 0;
		int cr = (b & (BIT_6 + BIT_5 + BIT_4)) >> 4;
		boolean sorted = (b & BIT_3) != 0;
		final int pixel = b & (BIT_2 + BIT_1 + BIT_0);
		paletteSize = (int) Math.pow(2, pixel + 1);
		picture.setPictureBackground(in.read());
		int ratio = in.read(); // ratio

		if (DEBUG_READ) {
			debug(1, "width = " + width);
			debug(1, "height = " + height);
			debug(1, "globalMapFollows = " + globalMapFollows);
			debug(1, "cr = " + cr);
			debug(1, "sorted = " + sorted);
			debug(1, "paletteSize = " + paletteSize + " - read: " + pixel);
			debug(1, "background = " + picture.getPictureBackground());
			debug(1, "ratio = " + ratio);
		}

		return globalMapFollows;
	}

	private final Palette readPalette() throws Exception {
		Palette pal = new Palette(picture);
		for (int i = 0; i < paletteSize; i++) {
			pal.addQuiet(new Color(in.read(), in.read(), in.read()));
		}
		if (DEBUG_READ)
			debug(0, "found palette with " + paletteSize + " colours");
		return pal;
	}

	public static void main(String[] args) {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Log.addLogListener(listener);
		readGIF("/home/michaela/tmp/test1.gif");
	}

	private final class GraphicControlExtension {
		public boolean useTransparentColour = false;
		public int transparentColour = -1;
		public int delay = -1;
		public int disposalMethod = -1;
	}
}
