package jdraw.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import jdraw.Main;
import jdraw.action.*;
import jdraw.data.DataChangeListener;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.data.event.ChangeEvent;
import jdraw.gui.undo.UndoManager;
import util.Log;
import util.ResourceLoader;
import util.gui.GUIUtil;
import util.gui.StandardMainFrame;

public final class MainFrame
	extends StandardMainFrame
	implements DataChangeListener {

	public static final Font DEFAULT_FONT = GUIUtil.DEFAULT_FONT;
	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font TINY_FONT = new Font("SansSerif", Font.PLAIN, 9);
	public static final Font BOLD_FONT = GUIUtil.BOLD_FONT;
	public static final Font BIG_FONT = new Font("Serif", Font.BOLD, 32);

	private static final String PREFIX = Main.APP_NAME + " " + Main.VERSION;

	public static final MainFrame INSTANCE = new MainFrame();

	private String fileName = null;
	private Picture picture;

	private final JPanel centerPanel = new JPanel(new BorderLayout(0, 0));

	private MainFrame() {
		super(PREFIX);
		setIconImage(ResourceLoader.getImage("jdraw/images/pixel_tool.png").getImage());

		setJMenuBar(new DrawMenu()); // compone i menu
		updateTitle();
		createGui();
		JPanel glassPane = new JPanel();
		glassPane.setLayout(null);
		glassPane.setOpaque(false);
		glassPane.add(InfoClip.INSTANCE);
		setGlassPane(glassPane);
		getGlassPane().setVisible(true);
	}

	private void attach() {
		picture.addDataChangeListener(this);

		final int frames = picture.getFrameCount();
		jdraw.data.Frame f;
		for (int i = 0; i < frames; i++) {
			f = picture.getFrame(i);
			f.addDataChangeListener(this);
			f.getPalette().addDataChangeListener(this);
		}
	}

	private void detach() {
		if (picture != null) {
			picture.removeDataChangeListener(this);
			final int frames = picture.getFrameCount();
			jdraw.data.Frame f;
			for (int i = 0; i < frames; i++) {
				f = picture.getFrame(i);
				f.removeDataChangeListener(this);
				f.getPalette().removeDataChangeListener(this);
			}
		}
	}

	public void setPicture(Picture pic) {
		final boolean updateOnly = (pic == picture);

		if (!updateOnly) {
			detach();
			picture = pic;
		}

		DrawAction.getAction(ReduceColoursAction.class).setEnabled(
			picture.getMaximalPaletteSize() > Palette.GIF_MAX_COLOURS);
		DrawAction.getAction(ReducePaletteColoursAction.class).setEnabled(
			Tool.getCurrentPalette().size() > Palette.GIF_MAX_COLOURS);
		FolderPanel.INSTANCE.setPicture(picture);
		PalettePanel.INSTANCE.setPalette(picture.getPalette());

		UndoManager.INSTANCE.reset();
		updateTitle();
		if (!updateOnly) {
			attach();
		}
	}

	public Picture getPicture() {
		return picture;
	}

	private final JPanel getMainPanel() {
		return (JPanel) getContentPane();
	}

	protected void createGui() {
		JPanel mainPanel = getMainPanel();
		mainPanel.add(StatusPanel.INSTANCE, BorderLayout.SOUTH); // barra inferiore dove sono le indicazioni del mouse
		centerPanel.add(FolderPanel.INSTANCE, BorderLayout.CENTER);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		showViews(true);
	}

	public void showViews(boolean flag) {
		if (flag) {
			getMainPanel().add(PalettePanel.INSTANCE, BorderLayout.WEST);
			centerPanel.add(ToolPanel.INSTANCE, BorderLayout.NORTH);
		}
		else {
			getMainPanel().remove(PalettePanel.INSTANCE);
			centerPanel.remove(ToolPanel.INSTANCE);
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String aName) {
		fileName = aName;
		updateTitle();
	}

	protected void updateTitle() {
		String title = fileName;
		if (title == null) {
			title = "programmaSenzaNome";
		}
		title = "[" + title + "] ";
		if (picture != null) {
			title =
				title
					+ "("
					+ String.valueOf(picture.getWidth())
					+ "x"
					+ String.valueOf(picture.getHeight())
					+ ", "
					+ Tool.getCurrentPalette().size()
					+ " colours"
					+ ")";
		}
		setTitle(PREFIX + "  -  " + title);
	}

	private DrawAction handleCtrlKeys(char c) {
		switch (c) {
			case 0x01 : // Ctrl-A
				return DrawAction.getAction(SaveAsAction.class);
			case 0x05 : // Ctrl-E
				return DrawAction.getAction(EditFrameSettingsAction.class);
			case 0x07 : // Ctrl+G
				return DrawAction.getAction(ToggleGridAction.class);
			case 0x08 : // Ctrl-H
				return DrawAction.getAction(HelpAction.class);
			case 0x0b : // Ctrl-K
				return DrawAction.getAction(CompressAction.class);
			case 0x0c : // Ctrl-L
				return DrawAction.getAction(LoadAction.class);
			case 0x0e : // Ctrl-N
				return DrawAction.getAction(NewAction.class);
			case 0x10 : // Ctrl+P
				return DrawAction.getAction(SetPixelToolAction.class);
			case 0x12 : // Ctrl-R
				return DrawAction.getAction(ResizeAction.class);
			case 0x13 : // Ctrl-S
				return DrawAction.getAction(SaveAction.class);
			case 0x16 : // Ctrl-V
				return DrawAction.getAction(ViewAnimationAction.class);
			case 0x1a : // Ctrl-Z
				return DrawAction.getAction(UndoAction.class);
			default :
				return null;
		}
	}

	private DrawAction handleCtrlShiftKeys(char c) {
		switch (c) {
			case 0x1a : // Ctrl-Shift-Z
				return DrawAction.getAction(RedoAction.class);
			default :
				return null;
		}
	}

	private DrawAction handlePlainKeys(char c) {
		switch (c) {
			case '+' :
				return DrawAction.getAction(IncreaseZoomAction.class);
			case '-' :
				return DrawAction.getAction(DecreaseZoomAction.class);
			case '0' :
				return DrawAction.getAction(SetMinZoomAction.class);
			case '9' :
				return DrawAction.getAction(SetMaxZoomAction.class);
			case '8' :
				return DrawAction.getAction(SetPreviousZoomAction.class);
			case ' ' :
				return DrawAction.getAction(ToggleTransparencyAction.class);
			case 'h' :
				return DrawAction.getAction(ToggleViewsAction.class);
			case 0x1b : // ESC
				return DrawAction.getAction(SetPixelToolAction.class);
			default:
				return null;
		}		
	}

	private DrawAction handleShiftKeys(char c) {
		return null;
	}

	public boolean handleKey(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_TYPED) {
			if ((e.getModifiers() & KeyEvent.ALT_MASK) > 0) {
				return false;
			}
			DrawAction action = null;

			switch (e.getModifiers()) {
				case KeyEvent.CTRL_MASK :
					action = handleCtrlKeys(e.getKeyChar());
					break;
				case 0 :
					action = handlePlainKeys(e.getKeyChar());
					break;
				case KeyEvent.SHIFT_MASK :
					action = handleShiftKeys(e.getKeyChar());
					break;
				case KeyEvent.SHIFT_MASK + KeyEvent.CTRL_MASK :
					action = handleCtrlShiftKeys(e.getKeyChar());
					break;
				default :
					return false;
			}
			if (action != null) {
				action.actionPerformed();
				e.consume();
				return true;
			}
			else {
				if (Log.DEBUG)
					Log.debug(
						"unknown key: " + keyDesc(e.getKeyChar(), e.getModifiers()));
			}
		}
		return false;
	}

	public void dataChanged(ChangeEvent e) {
		Dispatcher.dispatch(e);
	}

}
