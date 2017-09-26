package jdraw.action;

import jdraw.gui.ClipPanel;
import jdraw.gui.FolderPanel;
import jdraw.gui.PixelTool;
import jdraw.gui.Tool;
import jdraw.gui.ToolPanel;
import jdraw.gui.undo.UndoManager;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import util.Log;

/*
 * CropAction.java - created on 26.11.2003
 * 
 * @author Michaela Behling
 */

public class CropAction extends DrawAction {

	protected CropAction() {
		super("Crop Image", "crop.png");
		setToolTipText("Crops this image to the selected rectangle");
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		ClipPanel clipPanel =
			Tool.getCurrentFramePanel().getLayeredPane().getClipPanel();
		Rectangle r = Tool.getRealBounds(clipPanel.getBounds());
		int x = Math.max(0, r.x);
		int y = Math.max(0, r.y);
		int x2 = Math.min(Tool.getPictureWidth() - 1, r.x + r.width - 1);
		int y2 = Math.min(Tool.getPictureHeight() - 1, r.y + r.height - 1);
		int w = (x2 - x) + 1;
		int h = (y2 - y) + 1;

		if (Tool.getPicture().crop(x, y, w, h)) {
			setEnabled(false);
			UndoManager.INSTANCE.reset();		
			ToolPanel.INSTANCE.setCurrentTool(PixelTool.INSTANCE);	
			Log.info("Cropped.");
		}
	}

}
