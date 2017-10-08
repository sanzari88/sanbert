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

import util.gui.GUIUtil;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public class DrawPanel extends JPanel {

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

	public final void paintClip(
		Graphics gr,
		final boolean drawTransparentColour) {
		Graphics g = GUIUtil.createGraphics(gr);
		Palette pal = Tool.getCurrentPalette();
		final int transCol = Tool.getPicture().getTransparent();
		final boolean showGrid = showGrid();

		final int w = clip.getWidth();
		final int h = clip.getHeight();
		final int grid = getGrid();

		int x = 0;
		int y = 0;

		for (int row = 0; row < h; row++) {
			for (int column = 0; column < w; column++) {

				// pixel
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
				x = x + grid;
			}
			x = 0;
			y = y + grid;
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
		if (clip == null) {
			return super.getPreferredSize();
		}
		return new Dimension(
			(getGrid() * clip.getWidth()) + 1,
			(getGrid() * clip.getHeight()) + 1);
	}

}