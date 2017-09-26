package jdraw.action;

import jdraw.gui.DrawDialog;
import jdraw.gui.MainFrame;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import util.Log;
import util.ResourceLoader;
import util.Util;
import util.gui.GUIUtil;

/*
 * IntenseDrawAction.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public abstract class BlockingDrawAction extends DrawAction {

	private DrawDialog dialog;

	public BlockingDrawAction(String name) {
		super(name);
	}

	public BlockingDrawAction(String name, String iconName) {
		super(name, iconName);
	}

	public void setBusyCursor() {
		MainFrame.INSTANCE.setCursor(Cursor.WAIT_CURSOR);
	}

	public void setDefaultCursor() {
		MainFrame.INSTANCE.setCursor(Cursor.DEFAULT_CURSOR);
	}

	public abstract boolean prepareAction();
	public abstract void startAction();
	public abstract void finishAction();

	public void prepare() {
		Log.info("Please wait...");
		dialog = new DrawDialog("Please wait...", true);
		dialog.setModal(false);
		dialog.getContentPane().add(
			new JLabel(ResourceLoader.getImage("jdraw/images/busy.png")),
			BorderLayout.CENTER);
		dialog.open();
	}

	public void start() {
		startAction();
		actionFinished();
	}

	private void finish() {
		dialog.close();
		Log.info("");
	}

	public final void actionPerformed(ActionEvent e) {
		if (!prepareAction()) {
			return;
		}
		Thread t = new Thread() {
			public void run() {
				GUIUtil.invokeLater(BlockingDrawAction.this, "setBusyCursor");
				Util.delay(200);
				GUIUtil.invokeLater(BlockingDrawAction.this, "prepare");
				Util.delay(400);				
				GUIUtil.invokeLater(BlockingDrawAction.this, "start");
			}
		};
		t.start();
	}

	public void actionFinished() {
		finish();
		GUIUtil.invokeLater(this, "setDefaultCursor");
		GUIUtil.invokeLater(this, "finishAction");
	}

}
