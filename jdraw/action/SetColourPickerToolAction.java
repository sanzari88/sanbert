package jdraw.action;

import jdraw.gui.ColourPickerTool;
import jdraw.gui.FolderPanel;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/*
 * SetPixelToolAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public class SetColourPickerToolAction extends DrawAction {

	protected SetColourPickerToolAction() {
		super("Colour Picker Tool", "colorpicker.png");
		setToolTipText("Seleziona colore da sfondo o da or background colour");		
	}

	public void actionPerformed(ActionEvent e) {
		ToolPanel.INSTANCE.setCurrentTool(new ColourPickerTool());
	}

}
