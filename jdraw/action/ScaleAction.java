package jdraw.action;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import jdraw.data.Frame;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.gio.GIFReader;
import jdraw.gio.GIFWriter;
import jdraw.gio.ImageReader;
import jdraw.gio.PNGWriter;
import jdraw.gui.DrawBrowser;
import jdraw.gui.MainFrame;
import jdraw.gui.PixelTool;
import jdraw.gui.ScaleDialog;
import jdraw.gui.Tool;
import jdraw.gui.ToolPanel;
import jdraw.gui.undo.UndoManager;
import util.Log;
import util.ResourceLoader;
import util.Util;
import util.gui.GUIUtil;

/*
 * ScaleAction.java - created on 11.12.2003
 * 
 * @author Michaela Behling
 */

public final class ScaleAction extends BlockingDrawAction {

	private Dimension dimension;
	private int scaleStyle = 0;

	protected ScaleAction() {
		super("Scale Image...");
		setToolTipText("Scales this picture");
	}

	public boolean prepareAction() {
		ScaleDialog dialog = new ScaleDialog();
		dialog.open();
		if (dialog.getResult() == ScaleDialog.APPROVE) {
			dimension = dialog.getScalingDimension();
			scaleStyle = dialog.getScalingStyle();
			return (dimension.width != Tool.getPictureWidth())
				|| (dimension.height != Tool.getPictureHeight());
		}
		return false;
	}

	public void startAction() {
		Tool.getPicture().scale(dimension, scaleStyle);
		UndoManager.INSTANCE.reset();		
	}

	public void finishAction() {
		Log.info("Image scaled.");
	}
}
