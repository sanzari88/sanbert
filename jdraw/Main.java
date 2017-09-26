package jdraw;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import jdraw.data.Picture;
import jdraw.gui.DrawDialog;
import jdraw.gui.MainFrame;
import jdraw.gui.ToolPanel;
import util.Log;
import util.ResourceFinder;
import util.ResourceLoader;
import util.SimpleLogListener;
import util.Util;
import util.gui.FileBrowserUI;

/*
 * Main.java - created on 27-09/17
 * 
 * @author Raffaele Sanzari
 */

public final class Main {

	private static final int SPLASH_DELAY = 1000;
	static { // statische initialisierungen
		SimpleLogListener listener = new SimpleLogListener(System.out);
		Log.addLogListener(listener);
		if (System.getProperty("log2file") != null) {
			try {

				Log.addLogListener(
					new SimpleLogListener(
						new PrintStream(
							new FileOutputStream(
								System.getProperty("user.dir")
									+ File.separatorChar
									+ "jdraw.log"))));
			}
			catch (FileNotFoundException e) {
				Log.exception(e);
			}
		}
	};

	private Main() {
	}

	public static final String APP_NAME = "Automazione Tessile Raffaele Sanzari";
	public static final String VERSION = "v1.0 alfa";
	public static final String EMAIL =
		"<font color=blue>sanzari88@gmail.com</font>";
	public static final String WWW = "<font color=blue>www.sanbert.it</font>";
	public static final String WWW_JDRAW = WWW + "/de/software/jdraw";
	public static final String SF_WWW_JDRAW = "jdraw.sourceforge.net";

	private static void setupUI() {

		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			Object[] uiDefaults =
				{
					"FileChooserUI",
					FileBrowserUI.FILE_BROWSER_UI,
					"TitledBorder.font",
					new FontUIResource(MainFrame.DEFAULT_FONT)};
			UIManager.getDefaults().putDefaults(uiDefaults);

		}
		catch (Exception e) {
			Log.exception(e);
		}
	}

	private static void preload() {
		String[] exts = new String[] { ".gif", ".png" };
		String prefix = "jdraw/images";
		ResourceFinder finder = new ResourceFinder(prefix, exts);
		String[] files = finder.findResources();
		prefix = finder.getPath();
		final int len = files.length;
		String resource;
		for (int i = 0; i < len; i++) {
			resource = files[i].substring(files[i].lastIndexOf(prefix));
			ResourceLoader.getImage(resource);
		}
	}

	public static boolean isVisible(Picture pic) {
		return pic == MainFrame.INSTANCE.getPicture();
	}

	public static final void setPicture(Picture pic) {
		MainFrame.INSTANCE.setPicture(pic);
	}

	public static final void main(String[] args) {
		ResourceLoader.scalingHint = Image.SCALE_SMOOTH;
		setupUI();

		DrawDialog splash = new DrawDialog(null, APP_NAME + " Splash", true);
		splash.setModal(false);
		ImageIcon icon = ResourceLoader.getImage("jdraw/images/logo.png");
		splash.getContentPane().add(new JLabel(icon));
		splash.open();

		preload();
		Picture picture = Picture.createDefaultPicture(); // qui setta le dimensioni del disegno
		setPicture(picture);
		ResourceLoader.getImage("jdraw/images/background.gif");

		Util.delay(SPLASH_DELAY);
		splash.close();
		ToolPanel.INSTANCE.getCurrentTool().activate();
		MainFrame.INSTANCE.setVisible(true);

		Log.info("Buona programmazione Raf!");
	}

}
