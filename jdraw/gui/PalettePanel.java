package jdraw.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jdraw.action.DrawAction;
import jdraw.action.EditColourAction;
import jdraw.data.ColourEntry;
import jdraw.data.Palette;
import jdraw.data.Picture;
import jdraw.data.event.ChangeEvent;
import util.Assert;
import util.Log;
import util.Util;
import util.gui.ScrollablePanel;
import util.gui.StandardScrollPane;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public final class PalettePanel
	extends JPanel
	implements MouseListener, MouseMotionListener {

	public static final PalettePanel INSTANCE = new PalettePanel();

	private static final int COLS = 8;
	private static final int BORDER = 1;

	private Palette palette;
	private final JLabel titleLabel = new JLabel(" ");

	private JPanel colours = new JPanel(new GridLayout(0, COLS, BORDER, BORDER));
	private JPanel title;

	private final StandardScrollPane scrollPanel;

	private PalettePanel() {
		super(new BorderLayout(0, 0));

		JPanel p = new ScrollablePanel(ColourPanel.DIMENSION.width + 1, 5);
		p.setLayout(new BorderLayout(0, 0));
		p.add(colours, BorderLayout.NORTH);
		p.setBackground(Color.black);
		final int leftRight = 2;
		scrollPanel =
			new StandardScrollPane(
				p,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setEnsuredWidth(
			ColourPanel.DIMENSION.width * COLS
				+ (COLS * BORDER)
				+ leftRight
				+ leftRight);
		add(scrollPanel, BorderLayout.CENTER);

		add(GradientPanel.INSTANCE, BorderLayout.SOUTH);

		title = createTitleBar();
		title.addMouseListener(this);
		scrollPanel.getViewport().setBackground(Color.black);
		colours.setBackground(Color.black);
		colours.setBorder(new EmptyBorder(2, leftRight, 2, leftRight));
	}

	private JPanel createTitleBar() {
		JPanel titlePanel = new JPanel(new BorderLayout(0, 0));
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.setFont(MainFrame.BOLD_FONT);

		titleLabel.setForeground(Color.white);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.setBackground(new Color(40, 60, 160));
		add(titlePanel, BorderLayout.NORTH);
		Dimension dim = titleLabel.getPreferredSize();
		dim.height = dim.height + 2;
		titlePanel.setPreferredSize(dim);

		return titlePanel;
	}

	public void changeColour(ChangeEvent e) {
		updateColour(e.getOldInt());
		updateColour(e.getNewInt());
	}

	private void updateTitle() {
		StringBuffer buf = new StringBuffer();
		if (palette.isGlobalPalette()) {
			buf.append("Global ");
		}
		else {
			buf.append("Local ");
		}
		buf.append(" Palette");
		titleLabel.setText(buf.toString());
		MainFrame.INSTANCE.updateTitle();
	}

	public void setPalette(Palette aPalette) {
		palette = aPalette;
		createPaletteView();
		Picture pic = Tool.getPicture();
		updateColour(pic.getForeground());
		updateColour(pic.getBackground());
		updateColour(pic.getTransparent());
		updateTitle();
	}

	protected void colourAdded(ColourEntry e) {
		ColourPanel p = new ColourPanel(e);
		colours.add(p);
		p.addMouseListener(this);
		p.addMouseMotionListener(this);
		colours.invalidate();
		colours.revalidate();
	}

	private void createPaletteView() {
		setVisible(false);
		colours.removeAll();
		final int size = palette.size();
		for (int i = 0; i < size; i++) {
			colourAdded(palette.getColour(i));
		}
		setVisible(true);
	}

	public void updateColour(int index) {
		if (index != -1) {
			getColourPanel(index).update();
		}
	}

	private ColourPanel getColourPanel(int index) {
		return (ColourPanel) colours.getComponent(index);
	}

	private void editColour(Color col) {
		DrawAction.getAction(EditColourAction.class).actionPerformed();
	}

	public void mousePressed(MouseEvent e) {
		final int button = e.getButton();
		final int mod = e.getModifiers();

		if ((mod & KeyEvent.CTRL_MASK) > 0) {
			if (button == MouseEvent.BUTTON1) {
				setTransparentColour(e);
			}
			else if (
				(button == MouseEvent.BUTTON2) || (button == MouseEvent.BUTTON3)) {
				setPictureBackgroundColour(e);
			}
			return;
		}

		if (e.getSource() instanceof ColourPanel) {
			colourSelection(button, e);
		}
		else if (e.getSource() == title) {
			paletteMenu(button, e);
		}
	}

	private void setTransparentColour(MouseEvent e) {
		if (e.getSource() instanceof ColourPanel) {
			ColourPanel p = (ColourPanel) e.getSource();
			Picture pic = Tool.getPicture();
			if (pic.getTransparent() != p.index) {
				pic.setTransparent(p.index);
			}
			else {
				pic.setTransparent(-1);
			}
		}
	}

	private void setPictureBackgroundColour(MouseEvent e) {
		if (e.getSource() instanceof ColourPanel) {
			if (palette.isGlobalPalette()) {
				ColourPanel p = (ColourPanel) e.getSource();
				Picture picture = Tool.getPicture();
				picture.setPictureBackground(p.index);
			}
			else {
				Log.warning("Choose picture background in global palette!");
			}
		}
	}

	private void paletteMenu(int button, MouseEvent e) {
		JPopupMenu menu = createPaletteMenu();
		menu.show(title, e.getX(), 10);
	}

	private JPopupMenu createPaletteMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenu paletteMenu =
			((DrawMenu) MainFrame.INSTANCE.getJMenuBar()).getPaletteMenu();

		Component[] items = paletteMenu.getMenuComponents();
		Component o;
		for (int i = 0; i < items.length; i++) {
			o = items[i];
			if (o instanceof JMenuItem) {
				menu.add(new JMenuItem(((JMenuItem) o).getAction()));
			}
			else {
				menu.addSeparator();
			}

		}
		return menu;
	}

	private void colourSelection(int button, MouseEvent e) {
		ColourPanel p = (ColourPanel) e.getSource();

		switch (button) {
			case MouseEvent.BUTTON1 : // links klick
				Tool.getPicture().setForeground(p.index);
				// Doppelklick?
				if (e.getClickCount() == 2) {
					editColour(p.getBackground());
				}
				break;
			case MouseEvent.BUTTON2 : // rechts klick
			case MouseEvent.BUTTON3 : // rechts klick
				Tool.getPicture().setBackground(p.index);
				break;
			default :
				Assert.fail("gui: unknown button " + button);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	//	MouseMotionListener
	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		StatusPanel.INSTANCE.drawInfo(drawInfo(e.getSource()));
	}

	private final String drawInfo(Object o) {
		if (o instanceof ColourPanel) {
			ColourPanel cp = (ColourPanel) o;
			Color c = cp.getForeground();
			final int r = c.getRed();
			final int g = c.getGreen();
			final int b = c.getBlue();
			final int a = c.getAlpha();
			StringBuffer buf = new StringBuffer("Colour #");
			buf.append(cp.index);
			buf.append(" rgba(");
			buf.append(r);
			buf.append(",");
			buf.append(g);
			buf.append(",");
			buf.append(b);
			buf.append(",");
			buf.append(a);
			buf.append(") ");
			buf.append(" hex(");
			buf.append(Util.hexString(r, 2));
			buf.append(",");
			buf.append(Util.hexString(g, 2));
			buf.append(",");
			buf.append(Util.hexString(b, 2));
			buf.append(",");
			buf.append(Util.hexString(a, 2));
			buf.append(")");
			return buf.toString();
		}
		else {
			return "";
		}
	}

}
