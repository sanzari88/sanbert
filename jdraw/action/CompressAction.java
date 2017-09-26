package jdraw.action;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import jdraw.gui.Tool;
import util.Log;

/*
 * SaveAction.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class CompressAction extends BlockingDrawAction {

	private int freedColours;

	private String fileName;
	private boolean success;

	protected CompressAction() {
		super("Compress");
		setToolTipText("Optimizes the used palettes");
		setAccelerators(
			new KeyStroke[] {
				 KeyStroke.getKeyStroke(new Character('K'), KeyEvent.CTRL_MASK)});
	}

	public boolean prepareAction() {
		return true;
	}

	public void startAction() {
		freedColours = Tool.getPicture().compress();
	}

	public static void showResult(int freedCols) {
		switch (freedCols) {
			case 0 :
				Log.info("No colours were freed.");
				break;
			case 1 :
				Log.info("One colour was freed.");
				break;
			default :
				Log.info(String.valueOf(freedCols) + " colours were freed.");
		}
	}

	public void finishAction() {
		showResult(freedColours);
	}
}
