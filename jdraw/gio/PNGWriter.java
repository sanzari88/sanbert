package jdraw.gio;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import jdraw.data.ColourEntry;
import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Log;
import util.SimpleLogListener;
import util.Util;

/*
 * PNGWriter.java - created on 27.11.2003
 * 
 * @author Michaela Behling
 */

public final class PNGWriter {

	private static final boolean DEBUG_WRITE = true;

	private static final int[][] INTERLACED_PATTERN =
		{ { 1, 6, 4, 6, 2, 6, 4, 6 }, {
			7, 7, 7, 7, 7, 7, 7, 7 }, {
			5, 6, 5, 6, 5, 6, 5, 6 }, {
			7, 7, 7, 7, 7, 7, 7, 7 }, {
			3, 6, 4, 6, 3, 6, 4, 6 }, {
			7, 7, 7, 7, 7, 7, 7, 7 }, {
			5, 6, 5, 6, 5, 6, 5, 6 }, {
			7, 7, 7, 7, 7, 7, 7, 7 }
	};

	private static final int[] Y_OFFSET = { 0, 0, 4, 0, 2, 0, 1, 0 };
	private static final int MAX_LOOP = 7;

	private final Picture picture;
	private final OutputStream out;
	private final Frame frame;
	private final Palette pal;
	private final CRC32 crc32 = new CRC32();
	private final int transIndex;
	private final int paletteSize;
	private final int transColourCount;
	private final boolean interlaced;

	private int bitDepth;
	private int colourType;
	private int colourLength;

	final int width;
	final int height;

	private int[] scanline;
	private byte[] rgbScanline;
	private int x = 0, y = 0, loop = 1;

	private PNGWriter(
		Picture aPicture,
		OutputStream o,
		boolean writeInterlaced) {
		picture = aPicture;
		interlaced = writeInterlaced;
		frame = picture.getCurrentFrame();
		pal = picture.getCurrentPalette();
		width = picture.getWidth();
		height = picture.getHeight();
		paletteSize = pal.size();
		transIndex = picture.getTransparent();
		transColourCount = pal.countTransparentColours();
		out = o;
	}

	public static boolean writePNG(
		Picture pic,
		String fileName,
		boolean isInterlaced) {
		try {
			OutputStream o =
				new BufferedOutputStream(new FileOutputStream(fileName));
			PNGWriter writer = new PNGWriter(pic, o, isInterlaced);
			writer.writePNG();
			return true;
		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	//	used when scaling
	public static boolean writePNG(Frame aFrame, OutputStream o) {
		Picture pic = new Picture(aFrame.getWidth(), aFrame.getHeight());
		Palette p = aFrame.getPalette();
		pic.setPalette(p);
		aFrame.setPalette(null);
		pic.addFrame(aFrame);
		pic.setCurrentFrame(0);
		pic.setTransparent(aFrame.getTransparentColour());

		return writePNG(pic, o);
	}

	public static boolean writePNG(Picture pic, String fileName) {
		return writePNG(pic, fileName, false);
	}

	public static boolean writePNG(
		Picture pic,
		OutputStream o,
		boolean isInterlaced) {
		try {
			PNGWriter writer = new PNGWriter(pic, o, isInterlaced);
			writer.writePNG();
			return true;
		}
		catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	public static boolean writePNG(Picture pic, OutputStream o) {
		return writePNG(pic, o, false);
	}

	private void nextY() {
		do {
			y++;
			if (y < height) {
				int[] pattern = INTERLACED_PATTERN[y % (MAX_LOOP + 1)];
				for (int i = 0; i < pattern.length; i++) {
					if (pattern[i] == loop) {
						return;
					}
				}
			}
		}
		while (y < height);
		loop++;
		y = Y_OFFSET[loop - 1];
	}

	private boolean nextX() {
		final int yOff = y % (MAX_LOOP + 1);
		final int[] pattern = INTERLACED_PATTERN[yOff];
		int xOffset;
		int pixelForLoop;
		while (x < width) {
			xOffset = x % (MAX_LOOP + 1);
			pixelForLoop = pattern[xOffset];
			if (loop == pixelForLoop) {
				return true;
			}
			x++;
		}
		x = 0;
		nextY();
		return false;
	}

	private int nextInterlacedScanline() {
		if (loop <= MAX_LOOP) {
			scanline[0] = 0; // no filtering
			int index = 0;
			while (nextX()) {
				index++;
				scanline[index] = frame.getPixel(x, y);
				x++;
			}
			return index + 1;
		}
		else {
			return -1;
		}
	}

	private int nextPlainScanline() {
		if (y < height) {
			scanline[0] = 0; // no filtering			
			for (int i = 0; i < width; i++) {
				scanline[i + 1] = frame.getPixel(i, y);
			}
			y++;
			return width + 1; // pixel+filterbyte
		}
		else {
			return -1;
		}
	}

	private int nextScanline() {
		if (interlaced) {
			return nextInterlacedScanline();
		}
		else {
			return nextPlainScanline();
		}
	}

	private void writePNG() throws Exception {
		try {
			writeSignature();
			writeHeader();
			scanline = new int[width + 1];
			if (colourType == 3) { // indexed palette
				writePalette();
				writeIndexedBackground();
				if (transColourCount > 0) {
					writeIndexedTransparency();
				}
				writeIndexedImageData();
			}
			else if (colourType == 2) { // true colour without alpha
				writeRGBBackground();
				writeRGBImageData(false);
			}
			else { // true colour with alpha
				writeRGBBackground();
				writeRGBImageData(true);
			}
			writeFooter();

		}
		finally { // resource schliessen		
			out.flush();
			Util.close(out);
		}
	}

	private void writeIndexedBackground() throws Exception {
		writeLong(1);

		crc32.reset();
		writeBytes("bKGD".getBytes());
		writeByte(picture.getBackground());

		writeCRC();
	}

	private void writeRGBBackground() throws Exception {
		writeLong(6);

		crc32.reset();
		writeBytes("bKGD".getBytes());
		Color col = pal.getColour(picture.getPictureBackground()).getColour();
		writeByte(0);
		writeByte(col.getRed());
		writeByte(0);
		writeByte(col.getGreen());
		writeByte(0);
		writeByte(col.getBlue());

		writeCRC();
	}

	private void writeIndexedTransparency() throws Exception {
		final int lastTransColour = pal.getLastTransparentColour();

		writeLong(lastTransColour + 1);
		crc32.reset();
		writeBytes("tRNS".getBytes());
		final int trans = picture.getTransparent();
		for (int i = 0; i <= lastTransColour; i++) {
			if (i == trans) {
				writeByte(ColourEntry.TRANSPARENT);
			}
			else {
				writeByte(pal.getColour(i).getColour().getAlpha());
			}
		}
		writeCRC();
	}

	private void writeFooter() throws Exception {
		writeLong(0);

		crc32.reset();
		writeBytes("IEND".getBytes());
		writeCRC();
	}

	private void writeIndexedImageData() throws Exception {
		byte[] data = getIndexedImageData();
		writeLong(data.length); // length

		crc32.reset();
		writeBytes("IDAT".getBytes());
		writeBytes(data);

		writeCRC();
	}

	private void writeRGBImageData(boolean includeAlpha) throws Exception {
		byte[] data = getRGBImageData(includeAlpha);
		writeLong(data.length); // length

		crc32.reset();
		writeBytes("IDAT".getBytes());
		writeBytes(data);

		writeCRC();
	}

	private byte[] getIndexedImageData() throws Exception {
		ByteArrayOutputStream o = new ByteArrayOutputStream(4096);
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION);
		DeflaterOutputStream dout = new DeflaterOutputStream(o, compressor);

		int dataLen = 0;
		do {
			dataLen = nextScanline();
			if (dataLen != -1) {
				for (int i = 0; i < dataLen; i++) {
					dout.write((byte) scanline[i]);
				}
			}
		}
		while (dataLen != -1);
		dout.finish();
		byte[] data = o.toByteArray();
		dout.close();
		return data;
	}

	private void nextRGBScanline(final int len) {
		rgbScanline[0] = 0; // kein filter
		int offset;
		Color col;
		int index;
		for (int i = 0; i < len - 1; i++) {
			offset = i * colourLength;
			index = scanline[i + 1];
			col = pal.getColour(index).getColour();
			rgbScanline[offset + 1] = (byte) col.getRed();
			rgbScanline[offset + 2] = (byte) col.getGreen();
			rgbScanline[offset + 3] = (byte) col.getBlue();
			if (colourLength == 4) {
				rgbScanline[offset + 4] =
					(byte) ((index == transIndex)
						? ColourEntry.TRANSPARENT
						: col.getAlpha());
			}
		}
	}

	private byte[] getRGBImageData(boolean includeAlpha) throws Exception {
		ByteArrayOutputStream o = new ByteArrayOutputStream(4096);
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION);
		DeflaterOutputStream dout = new DeflaterOutputStream(o, compressor);

		colourLength = 3;
		if (includeAlpha) {
			colourLength = 4;
		}
		rgbScanline = new byte[(width * colourLength) + 1];

		int rgbLen;
		int dataLen = 0;
		do {
			dataLen = nextScanline();
			if (dataLen != -1) {
				nextRGBScanline(dataLen);
				rgbLen = ((dataLen - 1) * colourLength) + 1;
				dout.write(rgbScanline, 0, rgbLen);
			}
		}
		while (dataLen != -1);

		dout.finish();
		byte[] data = o.toByteArray();
		dout.close();
		return data;
	}

	private void writeHeader() throws Exception {
		writeLong(13); // length of data block

		crc32.reset();
		writeBytes("IHDR".getBytes());

		// data 
		writeLong(picture.getWidth());
		writeLong(picture.getHeight());

		bitDepth = 8;
		colourType = 3;

		if (pal.size() > Palette.GIF_MAX_COLOURS) {
			bitDepth = 8; // TODO wann brauch ich depth 16?
			if (transColourCount > 0) {
				colourType = 6;
			}
			else {
				colourType = 2;
			}
		}

		writeByte(bitDepth);
		writeByte(colourType);
		writeByte(0); // compression type = deflate/inflate
		writeByte(0); // filter method = adaptive filtering		
		writeByte(interlaced ? 1 : 0); // interlace method = 0/1

		writeCRC();
	}

	private void writePalette() throws Exception {
		final int size = pal.size();
		writeLong(3 * size); // rgb-triplets

		crc32.reset();
		writeBytes("PLTE".getBytes());
		Color col;
		for (int i = 0; i < size; i++) {
			col = pal.getColour(i).getColour();
			writeByte(col.getRed());
			writeByte(col.getGreen());
			writeByte(col.getBlue());
		}

		writeCRC();
	}

	private void writeCRC() throws Exception {
		long crc = crc32.getValue(); // invertieren?
		writeLong(crc);
	}

	private void writeSignature() throws Exception {
		int[] data = { 137, 80, 78, 71, 13, 10, 26, 10 };
		for (int i = 0; i < data.length; i++) {
			out.write(data[i]);
		}
	}

	private void writeBytes(byte[] data) throws Exception {
		out.write(data);
		crc32.update(data);
	}

	private void writeByte(int i) throws Exception {
		out.write(i);
		crc32.update((byte) i);
	}

	private void writeLong(long i) throws Exception {
		byte a = (byte) (i >> 24);
		byte b = (byte) (i >> 16);
		byte c = (byte) (i >> 8);
		byte d = (byte) i;

		writeByte(a);
		writeByte(b);
		writeByte(c);
		writeByte(d);
	}

	public static void main(String[] args) {
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Picture pic = ImageReader.readImage("C:/Java/Projects/JDraw/sheep.png");
		Log.addLogListener(listener);
		if (pic != null) {
			PNGWriter.writePNG(pic, "test.png");
			Log.debug("OK");
		}
	}
}
