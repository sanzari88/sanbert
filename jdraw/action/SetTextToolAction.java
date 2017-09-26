package jdraw.action;

import java.awt.Font;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gui.MainFrame;
import jdraw.gui.TextTool;
import jdraw.gui.Tool;
import jdraw.gui.ToolPanel;
import util.gui.FontDialog;

/*
 * SetMaxZoomAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class SetTextToolAction extends BlockingDrawAction {

	private Font font;
	private String text;
	private FontDialog dialog;
	
	
	protected SetTextToolAction() {
		super("Inserisci Testo", "text_tool.png");
		setToolTipText("Inserisci testo");
	}

	private FontDialog getDialog() {
		if (dialog == null) {
			dialog = new FontDialog(MainFrame.INSTANCE, "Font Dialog");
		}
		return dialog;
	}

	public boolean prepareAction() {
		font = null;
		ToolPanel.INSTANCE.setCurrentTool(TextTool.INSTANCE);
		FontDialog d = getDialog();

		ToggleAntialiasAction a =
			(ToggleAntialiasAction) DrawAction.getAction(
				ToggleAntialiasAction.class);
		Palette pal = Tool.getCurrentPalette();
		Picture pic = Tool.getPicture();
		dialog.setAntialiased(a.antialiasOn());
		dialog.setFontForeground(pal.getColour(pic.getForeground()).getColour());
		dialog.setFontBackground(pal.getColour(pic.getBackground()).getColour());
		d.open();
		
		if (d.getResult() == FontDialog.APPROVE) {			
			a.setAntialias(d.isAntialiased());
		}
		font = d.getFont();
		text = d.getText();
		return (font != null) && (text != null) && (text.length() > 0);
	}

	public void startAction() {
		TextTool.INSTANCE.process(font, text);
	}

	public void finishAction() {

	}
}
