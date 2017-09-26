package jdraw.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import jdraw.action.*;
import jdraw.data.Palette;
import jdraw.data.Picture;
import util.Log;
import util.gui.FontDialog;
import util.gui.GUIUtil;

/*
 * DrawMenu.java - created on 29.10.2003
 * 
 * @author Michaela Behling
 */

public final class DrawMenu extends JMenuBar {

	private JMenu paletteMenu;

	public DrawMenu() {
		createFileMenu();
		createEditMenu();
		createViewMenu();
		createFrameMenu();
		createPaletteMenu();
		createHelpMenu();
		createDebugMenu();
	}

	public JMenu getPaletteMenu() {
		return paletteMenu;
	}

	private void createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.setMnemonic('f');

		menu.add(new DrawMenuItem(DrawAction.getAction(NewAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(LoadAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(SaveAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(SaveAsAction.class)));
		//menu.add(new DrawMenuItem(DrawAction.getAction(SaveCompressedAction.class)));

		add(menu);
	}

	private void createFrameMenu() {
		JMenu menu = new JMenu("Frames");
		menu.setMnemonic('m');

		menu.add(new DrawMenuItem(DrawAction.getAction(AddFrameAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(RemoveFrameAction.class)));
		menu.addSeparator();
		menu.add(
			new DrawMenuItem(DrawAction.getAction(EditFrameSettingsAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(InsertAction.class)));

		add(menu);
	}

	private void createEditMenu() {
		JMenu menu = new JMenu("Modifica");
		menu.setMnemonic('e');

		menu.add(new DrawMenuItem(DrawAction.getAction(UndoAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(RedoAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(ResizeAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(CropAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(ScaleAction.class)));
		menu.addSeparator();
		//menu.add(new DrawMenuItem(DrawAction.getAction(CompressAction.class)));
		menu.add(
			new DrawMenuItem(DrawAction.getAction(ReduceColoursAction.class)));
		menu.add(
			new DrawMenuItem(DrawAction.getAction(ResetAlphaValuesAction.class)));
		add(menu);
	}

	private void createViewMenu() {
		JMenu menu = new JMenu("Visualizza");
		menu.setMnemonic('v');

		menu.add(
			new DrawMenuItem(DrawAction.getAction(IncreaseZoomAction.class)));
		menu.add(
			new DrawMenuItem(DrawAction.getAction(DecreaseZoomAction.class)));
		menu.add(
			new DrawMenuItem(DrawAction.getAction(SetPreviousZoomAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(SetMaxZoomAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(SetMinZoomAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(ToggleGridAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(ToggleViewsAction.class)));
		menu.addSeparator();
		menu.add(
			new DrawMenuItem(
				DrawAction.getAction(ToggleTransparencyAction.class)));
		menu.addSeparator();
		menu.add(
			new DrawMenuItem(DrawAction.getAction(ViewAnimationAction.class)));
		add(menu);
	}

	private void createPaletteMenu() {
		JMenu menu = new JMenu("Palette");
		menu.setMnemonic('p');
		menu.add(
			new DrawMenuItem(
				DrawAction.getAction(ToggleLocalPaletteAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(AddColourAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(SwapColoursAction.class)));
		menu.add(
			new DrawMenuItem(DrawAction.getAction(RemoveColourAction.class)));
		menu.addSeparator();
		menu.add(new DrawMenuItem(DrawAction.getAction(SortPaletteAction.class)));
		menu.addSeparator();
		menu.add(
			new DrawMenuItem(DrawAction.getAction(CompressPaletteAction.class)));
		menu.add(
			new DrawMenuItem(
				DrawAction.getAction(ReducePaletteColoursAction.class)));
		menu.add(
			new DrawMenuItem(
				DrawAction.getAction(ResetPaletteAlphaValuesAction.class)));
		paletteMenu = menu;
		add(menu);
	}

	private void createHelpMenu() {
		JMenu menu = new JMenu("Help");
		menu.setMnemonic('h');

		menu.add(new DrawMenuItem(DrawAction.getAction(AboutAction.class)));
		menu.add(new DrawMenuItem(DrawAction.getAction(HelpAction.class)));
		add(menu);
	}

	private void createDebugMenu() {
		if (Log.DEBUG) {
			JMenu menu = new JMenu("Debug");
			menu.setMnemonic('D');

			JMenuItem item;

			final String text =
				"This is a simple test text!"
					+ " And so that we really see an effect, "
					+ "let's have a longer text passage right here."
					+ "\nAnd a new line starts here."
					+ "\n\nCan you see a blank line?";

			item = new JMenuItem("Show Warning");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIUtil.warning(MainFrame.INSTANCE, text);
				}
			});
			menu.add(item);

			item = new JMenuItem("Show Error");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIUtil.error(MainFrame.INSTANCE, text);
				}
			});
			menu.add(item);

			item = new JMenuItem("Show Info");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIUtil.info(MainFrame.INSTANCE, text);
				}
			});
			menu.add(item);

			item = new JMenuItem("Show Yes/No");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIUtil.yesNo(
						MainFrame.INSTANCE,
						"Do you really wanna click?",
						text);
				}
			});
			menu.add(item);

			menu.addSeparator();

			item = new JMenuItem("Show Font-Dialog...");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FontDialog d = new FontDialog(MainFrame.INSTANCE);
					Palette pal = Tool.getCurrentPalette();
					Picture pic = Tool.getPicture();
					d.setFontForeground(pal.getColour(pic.getForeground()).getColour());
					d.setFontBackground(pal.getColour(pic.getBackground()).getColour());					
					d.open();
				}
			});
			menu.add(item);

			menu.addSeparator();

			menu.add(
				new JMenuItem(
					GUIUtil.createSaveLookFeelPropertiesAction("properties.log")));

			add(menu);
		}
	}

	private class DrawMenuItem extends JMenuItem {
		public DrawMenuItem(DrawAction action) {
			super(action);
			this.setToolTipText(action.getToolTipText());
		}
	}

}
