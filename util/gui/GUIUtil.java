package util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import util.Log;
import util.ResourceLoader;
import util.Util;

/*
 * Util.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class GUIUtil {

	public static final String GEOMETRY_PARAM = "geometry";
	public static final Font DEFAULT_FONT =
		new Font("SansSerif", Font.PLAIN, 12);
	public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 12);

	private static final int DIALOG_ICON_SIZE = 48;
	private static final int DIALOG_TEXT_WIDTH =
		Toolkit.getDefaultToolkit().getScreenSize().width / 4;

	private static final Rectangle GEOMETRY = parseGeometry();

	private GUIUtil() {
	}

	public static Action createSaveLookFeelPropertiesAction(final String fileName) {
		Action action = new AbstractAction("Print Look&Feel properties") {
			public void actionPerformed(ActionEvent e) {
				File f = new File(fileName);
				saveLookFeelProperties(f);
			}
		};
		return action;
	}

	public static void waitForImage(ImageIcon icon) {
		while ((icon.getIconWidth() == -1) || (icon.getIconHeight() == -1)) {
			Util.delay(100);
		}
	}

	public static Graphics2D createGraphics(Graphics g) {
		Graphics2D co = (Graphics2D) g.create();
		return co;
	}

	public static int print(String title, Hashtable table, PrintStream out) {
		Iterator keys = table.keySet().iterator();
		int count = 0;
		out.println();
		out.println("Hashtable : " + title);
		out.println();
		Object key, value;
		while (keys.hasNext()) {
			count++;
			key = keys.next();
			value = table.get(key);
			out.println(
				"Key:   " + key + " (" + Util.shortClassName(key.getClass()) + ")");
			out.println(
				"Value: "
					+ table.get(key)
					+ " ("
					+ Util.shortClassName(value.getClass())
					+ ")");
			out.println();
		}
		return count;
	}

	public static void saveLookFeelProperties(File file) {
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(file));
			out.println(
				"Properties of Look&Feel '"
					+ UIManager.getLookAndFeel().getName()
					+ "'");
			int count = print("UIDefaults", UIManager.getDefaults(), out);
			count =
				count
					+ print(
						"Look&Feel Defaults",
						UIManager.getLookAndFeelDefaults(),
						out);
			if (Log.DEBUG)
				Log.debug(
					">> "
						+ count
						+ " properties written to "
						+ file.getAbsolutePath());
			out.flush();
		}
		catch (Exception e) {
			Log.exception(e);
		}
		finally {
			Util.close(out);
		}
	}

	public static boolean yesNo(StandardMainFrame parent, String text) {
		return question(parent, "Question", text, "Yes", "No");
	}

	public static boolean yesNo(
		StandardMainFrame parent,
		String title,
		String text) {
		return question(parent, title, text, "Yes", "No");
	}

	public static boolean question(StandardMainFrame parent, String text) {
		return question(parent, "Question", text, "OK", "Cancel");
	}

	public static void invokeLater(Object anObj, String methodName) {
		try {
			Method m = anObj.getClass().getMethod(methodName, null);
			invokeLater(anObj, m, new Object[0]);
		}
		catch (NoSuchMethodException e) {
			Log.error(
				"Can't find method "
					+ methodName
					+ " on "
					+ anObj.getClass()
					+ "\n"
					+ e.toString());
		}
	}

	public static void invokeLater(
		final Object anObj,
		final Method method,
		final Object[] params) {

		Runnable runner = new Runnable() {
			public void run() {
				try {
					method.invoke(anObj, params);
				}
				catch (Exception e) {
					Log.exception(e);
				}
			}
		};
		SwingUtilities.invokeLater(runner);
	}

	public static Component findParentComponentOfClass(
		Component component,
		Class c) {
		Component parent = component.getParent();
		while (parent != null) {
			if (c.isAssignableFrom(parent.getClass())) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public static boolean question(
		StandardMainFrame parent,
		String title,
		String text,
		String ok,
		String cancel) {
		int result =
			dialog(
				parent,
				title,
				text,
				ResourceLoader.getImage(
					"util/images/question.png",
					DIALOG_ICON_SIZE),
				ok,
				cancel);
		return (result == StandardDialog.APPROVE);
	}

	public static void info(StandardMainFrame parent, String text) {
		info(parent, text, "OK");
	}

	public static void info(StandardMainFrame parent, String text, String ok) {
		info(parent, "Info", text, ok);
	}

	public static void info(
		StandardMainFrame parent,
		String title,
		String text,
		String ok) {
		dialog(
			parent,
			title,
			text,
			ResourceLoader.getImage("util/images/info.png", DIALOG_ICON_SIZE),
			ok,
			null);
	}

	public static void warning(StandardMainFrame parent, String text) {
		warning(parent, "Warning", text, "OK");
	}

	public static void warning(
		StandardMainFrame parent,
		String text,
		String ok) {
		warning(parent, "Warning", text, ok);
	}

	public static void warning(
		StandardMainFrame parent,
		String title,
		String text,
		String ok) {
		dialog(
			parent,
			title,
			text,
			ResourceLoader.getImage("util/images/warning.png", DIALOG_ICON_SIZE),
			ok,
			null);
	}

	public static void error(StandardMainFrame parent, String text) {
		error(parent, text, "OK");
	}

	public static void error(StandardMainFrame parent, String text, String ok) {
		error(parent, "Error", text, ok);
	}

	public static void error(
		StandardMainFrame parent,
		String title,
		String text,
		String ok) {
		dialog(
			parent,
			title,
			text,
			ResourceLoader.getImage("util/images/error.png", DIALOG_ICON_SIZE),
			ok,
			null);
	}

	private static int dialog(
		StandardMainFrame parent,
		String title,
		String text,
		ImageIcon icon,
		String ok,
		String cancel) {

		if ((text == null) || (text.length() == 0)) {
			text = "<no text given>";
		}
		StandardDialog d = new StandardDialog(parent, title);
		d.addRightButton(d.getApproveButton());
		d.getApproveButton().setText(ok);
		d.getRootPane().setDefaultButton(d.getApproveButton());
		if (cancel != null) {
			d.addRightButton(d.getCancelButton());
			d.getCancelButton().setText(cancel);
		}
		d.addButtonPanel();
		d.setUndecorated(false);
		d.setDefaultBorder();
		d.setModal(true);
		JPanel p = new JPanel(new BorderLayout(12, 0));
		JLabel iconLabel = new JLabel(icon);
		p.add(iconLabel, BorderLayout.WEST);
		iconLabel.setVerticalAlignment(SwingConstants.TOP);
		JPanel p2 = new JPanel(new BorderLayout(0, 4));
		JLabel header = new JLabel(title);
		header.setFont(BOLD_FONT);
		p2.add(header, BorderLayout.NORTH);
		TextCalculator calc =
			new TextCalculator(DIALOG_TEXT_WIDTH, text, DEFAULT_FONT, true);
		JLabel label = calc.createLabel();
		label.setFont(DEFAULT_FONT);
		p2.add(label, BorderLayout.CENTER);
		p.add(p2, BorderLayout.CENTER);
		d.main.add(p, BorderLayout.CENTER);
		d.open();
		return d.getResult();
	}

	public static Rectangle getGeometry() {
		return GEOMETRY;
	}

	private static Rectangle parseGeometry() {
		Rectangle geom = null;
		String s = System.getProperty(GEOMETRY_PARAM);
		if (s != null) {
			StringTokenizer st = new StringTokenizer(s, "x+-", true);
			if (st.countTokens() != 7)
				Log.error("Invalid geometry expression \"" + s + "\"");
			String[] values = new String[7];
			for (int i = 0; i < 7; i++)
				values[i] = st.nextToken();
			if (!(values[1].equals("x")
				&& values[3].equals("+")
				&& values[5].equals("+")))
				Log.error("Invalid geometry separators in \"" + s + "\"");
			int[] dim = new int[4];
			for (int i = 0; i < 4; i++) {
				try {
					dim[i] = Integer.parseInt(values[i * 2]);
					if (dim[i] < 0)
						throw new RuntimeException(
							"Geometry value \"" + dim[i] + "\" out of range");
				}
				catch (Exception e) {
					Log.error("Invalid geometry dimension \"" + values[i] + "\"");
				}
			}
			geom = new Rectangle(dim[2], dim[3], dim[0], dim[1]);
		}
		if (geom == null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			dim.width = dim.width - 100;
			dim.height = dim.height - 100;
			geom = new Rectangle(0, 0, dim.width, dim.height);
		}
		return geom;
	}

}
