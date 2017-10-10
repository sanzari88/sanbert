package jdraw.gui;

import jdraw.data.Clip;
import jdraw.data.Palette;
import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Comandi.Comando;
import util.gui.GUIUtil;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public class DrawPanel extends JPanel {
	
	public static final int NUMERO_COMANDI = 6; // include 1 unità per il divisiorio... quindi i comandi sono 5

	private static final Color TRANS_COL = new Color(0, 0, 0, 0);

	protected Clip clip;

	public DrawPanel() {
		setOpaque(false);
	}

	public Clip getClip() {
		return clip;
	}

	public int getGrid() {
		return Tool.getGrid();
	}

	protected void setClip(Clip aClip) {
		clip = aClip;
		setPreferredSize(getPreferredSize());
	}

	public boolean showGrid() {
		return FolderPanel.INSTANCE.showGrid();
	}

	public final void paintClip(Graphics gr,final boolean drawTransparentColour) {
		
		Graphics g = GUIUtil.createGraphics(gr);
		Palette pal = Tool.getCurrentPalette();
		final int transCol = Tool.getPicture().getTransparent();
		final boolean showGrid = showGrid();

		final int w = clip.getWidth()-DrawPanel.NUMERO_COMANDI; // tolgo la parte dei comandi che la stampo dopo
		final int h = clip.getHeight();
		final int grid = getGrid();

		int x = 0;
		int y = 0;

		for (int row = 0; row < h; row++) {
			for (int column = 0; column < w; column++) {

				//if(column < w-2){// pixel
				int c = clip.getPixel(column, row);
				Maglia m = clip.getMaglia(column, row);
				
				if (c == transCol) {
					if (drawTransparentColour) {
						g.setColor(TRANS_COL);
						g.fillRect(x, y, grid, grid);
					}
				}
				else {
					ImageIcon imageLavoro = null;
					if(m!=null) {
					if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.values()[0].toString())) {
						imageLavoro = new ImageIcon("/Users/sanzariraffaele/Downloads/jdraw/jdraw/images/maglie/maglia_diritto.png");
						}
					else if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.values()[1].toString())) {
						imageLavoro = new ImageIcon("/Users/sanzariraffaele/Downloads/jdraw/jdraw/images/maglie/maglia_rovescio.png");
					}
					else if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.values()[2].toString())) {
						imageLavoro = new ImageIcon("/Users/sanzariraffaele/Downloads/jdraw/jdraw/images/maglie/ingleseAnt.png");
					}
					else if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.values()[3].toString())) {
						imageLavoro = new ImageIcon("/Users/sanzariraffaele/Downloads/jdraw/jdraw/images/maglie/inglesePost.png");
					}
					else if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.values()[4].toString())) {
						imageLavoro = new ImageIcon("/Users/sanzariraffaele/Downloads/jdraw/jdraw/images/maglie/maglia_unita.png");
					}
				}
					 
					g.setColor(pal.getColour(c).getColour()); 
					g.fillRect(x, y, grid, grid);
					
					if(imageLavoro!= null) {
					Image img = imageLavoro.getImage();
					
					//g.drawString("a", 5,5);
					
					g.drawImage(img,x , y, grid-5, grid-5, null);
					}
				}
				// grid
				if (showGrid) {
					g.setColor(Color.darkGray);
					g.drawRect(x, y, grid, grid);
				}
				
//				// gestisco colonna comandi
//				
//				if(column == 7) {
//					g.setColor(pal.getColour(7).getColour());
//				}
				x = x + grid;
			}
//				else {
//					g.setColor(Color.blue);
//					g.fillRect(x, y, grid, grid);
//					x = x + grid;
//				}
//		}
			x = 0;
			y = y + grid;
		}
		
		// disegno la matrice dei comandi
		paintComandi( gr, w);
	}
	
	private void paintComandi(Graphics gr,int larghezzaMatriceMaglie) {
		Palette pal = Tool.getCurrentPalette();
		Graphics g = GUIUtil.createGraphics(gr);
		Comando[][] matriceComandi= Clip.getMatriceComandi();
		int altezzaMatrice=matriceComandi.length;
		final int grid = getGrid();
		int x = larghezzaMatriceMaglie * grid;
		int y=0;
		
		// linea separazione comandi
			for(int j=0; j<altezzaMatrice;j++) {
				g.setColor(Color.WHITE);
				g.fillRect(x, y, grid-(grid/2), grid);
				y = y + grid;
				
			}
		
		x = (larghezzaMatriceMaglie * grid)+(grid/2);
		y=0;
		aggiornaColoreGuidafilo(matriceComandi);
		for(int i=0; i<NUMERO_COMANDI-1;i++) {
			for(int j=0; j<altezzaMatrice;j++) {
				Comando c = matriceComandi[j][i];
				if(c==null) {
					g.setColor(Color.BLACK);
					g.fillRect(x, y, grid, grid);
				}
				else {
					if(c.getComando().equalsIgnoreCase("Guidafilo")) {
						g.setColor(pal.getColour(c.getColore()).getColour());
						g.fillRect(x, y, grid, grid);
						if(c.getValue()!=null && c.getValue().length()>0) {
						g.setColor(Color.white);
						g.drawString(c.getValue(), x,y+grid);
						}
					}
					else if(c.getComando().equalsIgnoreCase("Gradazione")) {
						g.setColor(pal.getColour(c.getColore()).getColour());
						g.fillRect(x, y, grid, grid);
					}
					else if(c.getComando().equalsIgnoreCase("Tirapezza")) {
						g.setColor(pal.getColour(c.getColore()).getColour());
						g.fillRect(x, y, grid, grid);
					}
					else if(c.getComando().equalsIgnoreCase("Velocita")) {
						g.setColor(pal.getColour(c.getColore()).getColour());
						g.fillRect(x, y, grid, grid);
					}
				}
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(x, y, grid, grid);
				y = y + grid;
			}
			x = ((larghezzaMatriceMaglie+i) * grid)+(grid/2);
			y=0;
		}
			
	}
	
	private void aggiornaColoreGuidafilo(Comando[][] matriceComandi) {
		for(int i=0;i<matriceComandi.length;i++) {
			if(matriceComandi[i][1]!=null && matriceComandi[i][1].getValue()!=null)
				for(int j=0;i<matriceComandi.length;j++) {
					if(matriceComandi[j][1]!=null && matriceComandi[i][1].getColore()==matriceComandi[j][1].getColore())
						matriceComandi[i][1].setValue(matriceComandi[j][1].getValue());
				}
		}
	}

	public void setPreferredSize(Dimension dim) {
		super.setPreferredSize(dim);
		setBounds(0, 0, dim.width, dim.height);
	}

	public final void paint(Graphics g) {
		super.paint(g);

		if (clip == null) {
			return;
		}
		paintClip(g, false);
	}

	public Dimension getPreferredSize() {
		final int grid = getGrid();
		if (clip == null) {
			return super.getPreferredSize();
		}
		return new Dimension(
			(getGrid() * clip.getWidth()) + 1+ (NUMERO_COMANDI * grid),
			(getGrid() * clip.getHeight()) + 1);
	}

}