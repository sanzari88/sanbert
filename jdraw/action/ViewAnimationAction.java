package jdraw.action;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gio.GIFWriter;
import jdraw.gui.DrawDialog;
import jdraw.gui.MainFrame;
import jdraw.gui.Tool;
import util.Log;
import util.gui.GUIUtil;

/*
 * ResizeAction.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class ViewAnimationAction extends BlockingDrawAction {

	private boolean success;

	private ByteArrayOutputStream out;

	public ViewAnimationAction() {
		super("Compila", "view_anim.png");
		setToolTipText("Compila sorgente");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('V'), KeyEvent.CTRL_MASK)});
	}

	public static final boolean checkGIFColours() {
		return checkColours(
			Palette.GIF_MAX_COLOURS,
			"In GIF images the number of colours in each palette is limited to "
				+ 
				+ Palette.GIF_MAX_COLOURS
				+ ". This picture uses "
				+ String.valueOf(Palette.GIF_MAX_COLOURS)
				+ " colours.");
	}

	public static final boolean checkIconColours() {
		return checkColours(
			Palette.GIF_MAX_COLOURS,
			"At least one icon uses more than "
				+ Palette.GIF_MAX_COLOURS
				+ " colours, but JDraw does <b>not</b> support true colour"
				+ "icons.");
	}

	public static final boolean checkColours(int maxPalSize, String message) {
		Picture pic = Tool.getPicture();
		if ( pic.getMaximalPaletteSize() > maxPalSize) {
			if (GUIUtil
				.question(
					MainFrame.INSTANCE,
					"Too many colours!",
					message
						+ "\n\n"
						+ "Do you want to reduce the number of colours?",
					"Reduce Colours",
					"Cancel")) {
				pic.reduceColours();
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public boolean prepareAction() {
		return (checkGIFColours());
	}

	public void startAction() {
		out = new ByteArrayOutputStream();
		success = GIFWriter.writeGIF(Tool.getPicture(), out);
	}

	public void finishAction() {
		if (success) {
			final DrawDialog dialog = new DrawDialog("Animation");

			ImageIcon icon = new ImageIcon(out.toByteArray());
			JLabel label = new JLabel(icon);
			dialog.setModal(true);
			dialog.main.add(label, BorderLayout.CENTER);
			dialog.setUndecorated(false);
			dialog.setDefaultBorder();
			dialog.addRightButton(dialog.getApproveButton());
			dialog.getApproveButton().setText("Close");
			dialog.getApproveButton().setMnemonic('c');
			dialog.addButtonPanel();
			dialog.open();
			return;
		}
		Log.warning("Couldn't create animation.");
	}
}