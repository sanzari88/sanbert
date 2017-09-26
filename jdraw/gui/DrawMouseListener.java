package jdraw.gui;

import java.awt.event.MouseEvent;

/*
 * DrawMouseListener.java - created on 22.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public interface DrawMouseListener {
	
	public void mouseClicked(MouseEvent e);

	public void mouseEntered(MouseEvent e);

	public void mouseExited(MouseEvent e);
	
	public void mousePressed(MouseEvent e);

	public void mouseReleased(MouseEvent e);

	public void mouseDragged(MouseEvent e);

	public void mouseMoved(MouseEvent e);
}
