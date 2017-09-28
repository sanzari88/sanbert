package jdraw.data;

import java.awt.Rectangle;
import java.util.Arrays;

import javax.tools.Tool;

import jdraw.data.event.ChangeEvent;
import jdraw.gui.FolderPanel;
import jdraw.gui.MainFrame;
import magliera.puntoMaglia.Maglia;

/*
 * Clip.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class Clip extends DataObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum PUNTO_MAGLIA {RasatoDiritto, RasatoRovescio, MagliaUnitaANT, MagliaUnitaPOST, MagliaInglese};

	protected int transColour = -1;


	protected int[][] data;
	
	protected static Maglia [][] matriceMaglie;

	public Clip(final int width, final int height, int background) {
		data = new int[height][width];
		matriceMaglie = new Maglia [height][width];
		System.out.println("Matrice della maglia creata con successo");
		fill(background);
	}

	public final int getTransparentColour() {
		return transColour;
	}

	public final boolean usesTransparency() {
		return transColour != -1;
	}

	public void setTransparent(int index) {
		transColour = index;
	}

	protected final void setTransparentColourQuiet(int index) {
		transColour = index;
	}

	public final void fill(int colour) {
		final int height = getHeight();
		for (int i = 0; i < height; i++) {
			Arrays.fill(data[i], colour);
		}
	}

	public void setPixels(Pixel[] pixels) {
		setPixels(pixels, false);
	}

	public void restorePixels(Pixel[] pixels) {
		setPixels(pixels, true);
	}

	private void setPixels(final Pixel[] pixels, final boolean restore) {
		final int len = pixels.length;
		Pixel p;
		if (restore) {
			for (int i = 0; i < len; i++) {
				p = pixels[i];
				setPixelQuiet(p.x, p.y, p.oldColour);
			}
		}
		else {
			for (int i = 0; i < len; i++) {
				p = pixels[i];
				setPixelQuiet(p.x, p.y, p.newColour);
			}
		}
		notifyDataListeners(new ChangeEvent(this, FRAME_NEEDS_REPAINT));
	}

	public void pasteClip(Clip clip, int startX, int startY) {
		final int minX = Math.max(0, startX);
		final int minY = Math.max(0, startY);

		final int maxX = Math.min((startX + clip.getWidth()) - 1, getWidth() - 1);
		final int maxY =
			Math.min((startY + clip.getHeight()) - 1, getHeight() - 1);

		int col;		
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				col = clip.getPixel(x - startX, y - startY);			
				if (col != transColour) {
					setPixelQuiet(x, y, col);
				}
			}
		}
		notifyDataListeners(new ChangeEvent(this, CLIP_DATA_CHANGED));
	}

	public void setPixelQuiet(int x, int y, int col) {
		data[y][x] = col;
	}
	
	public void setMaglia(int x, int y, int colore, String tipoLavoro) {
		Maglia m = new Maglia(x,y,colore,tipoLavoro);
		matriceMaglie[y][x] = m;
		System.out.println("Punto maglia "+m.getTipoLavoro()+" inserito correttamente nella matrice delle maglie");
		System.out.println("X: "+m.getX() +" Y: "+m.getY());
	}

	public void setPixel(int x, int y, int col) {
			data[y][x] = col;
			setMaglia(x, y, col, jdraw.gui.Tool.getTipoLavoro());
			notifyDataListeners(
				new ChangeEvent(this, CLIP_PIXEL_CHANGED, x, y, col));
		
	}

	public Clip(int[][] dataField) {
		data = dataField;
	}

	public final Rectangle getBounds() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}

	public final int getWidth() {
		return data[0].length;
	}

	public final int getHeight() {
		return data.length;
	}

	public final int getPixel(int x, int y) {
		return data[y][x];
	}
	
	public final Maglia getMaglia(int x, int y) {
		return matriceMaglie[y][x];
	}
	
	public static  Maglia[][] getMatriceMaglia() {
		return matriceMaglie;
	}
	
	public static int getYInvertita(int y) {
		final int maxY = MainFrame.INSTANCE.getPicture().getWidth()-1;
		y = maxY-y;
		return y;
	}

	public static void setMatriceMaglie(Maglia[][] matriceMaglie) {
		Clip.matriceMaglie = matriceMaglie;
	}
	
	
}
