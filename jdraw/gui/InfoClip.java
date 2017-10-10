package jdraw.gui;

import jdraw.data.Clip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import util.Log;

/*
 * InfoClip.java - created on 22.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class InfoClip extends FloatingClip {

	public static final InfoClip INSTANCE = new InfoClip();

	private static final int X_OFFSET = 15;
	private static final int Y_OFFSET = -10;

	private final JLabel info = new JLabel();
	private MouseEvent lastEvent;
	private Point lastViewPoint;

	private InfoClip() {  // informazioni mouse sulla griglia
		super(DrawLayers.INFO_CLIP_LAYER);
		info.setFont(MainFrame.DEFAULT_FONT);
		info.setForeground(Color.red);
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(info);
	}

	public String getName() {
		return "Info";
	}

	private Point translate(MouseEvent e) {
		Point p =SwingUtilities.convertPoint(Tool.getDrawPanel(),e.getX(),e.getY(),MainFrame.INSTANCE.getGlassPane());				
		return p;
	}

	private String getInfo(MouseEvent e) {
		StringBuffer buf = new StringBuffer();
		int inizioComandi=Clip.getLarghezzaDisegno();
		Point p = Tool.getRealPixel(e);
		if (p == null) {
			return "";
		}
		if(p.x<inizioComandi) {
			buf.append("R: "+p.y);
			buf.append(" , ");
			buf.append("A: "+p.x);
			}
		else {
			int sceltaComando= p.x-inizioComandi;
			switch (sceltaComando) {
			case 1:buf.append("Guidafilo");
				break;
			case 2:buf.append("Gradazione");
			break;
			case 3:buf.append("Tirapezza");
			break;
			case 4:buf.append("Velocita");
			break;

			default:
				buf.append("Colonna non definita");
				break;
			}
		}
		return buf.toString();
	}

	private void updateInfo(MouseEvent e) {
		info.setText(getInfo(e));
		int dimensioneDisegno=Clip.getLarghezzaDisegno();
		Dimension dim = getPreferredSize();
		Point p = translate(e);		
		int x = p.x + X_OFFSET;
		int y = p.y + Y_OFFSET;
		
		Point p2 = Tool.getRealPixel(e);
		if(p2 != null && p2.x>dimensioneDisegno)
			defineClip(x-100, y, dim.width, dim.height); // posizione di dove stampera le info del mouse
		else
			defineClip(x, y, dim.width, dim.height); // posizione di dove stampera le info del mouse
	}

	public Integer getLayer() {
		return super.getLayer();
	}
	

	public void mouseDragged(MouseEvent e) {
		deactivate();		
		//updateInfo(e);
	}

	public void mouseEntered(MouseEvent e) {
		updateInfo(e);
	}

	public void mouseExited(MouseEvent e) {
		deactivate();
		lastEvent = null;
	}

	public void mouseMoved(MouseEvent e) {
		updateInfo(e);
		lastEvent = e;
		lastViewPoint = Tool.getViewPoint();
	}

	public void mousePressed(MouseEvent e) {
		deactivate();
		//updateInfo(e);
	}


	public final void paint(Graphics g) {
		if (isActive()) {
			g.setXORMode(Color.black);
			super.paint(g);			
			g.setPaintMode();	
		}
	}

	protected void repeat() {
		if (lastEvent != null) {
			Point p = Tool.getViewPoint();
			int xDiff = p.x - lastViewPoint.x;
			int yDiff = p.y - lastViewPoint.y;
			lastEvent.translatePoint(xDiff, yDiff);
			mouseMoved(lastEvent);
		}
	}

	public void mouseReleased(MouseEvent e) {
		updateInfo(e);
	}

}
