package jdraw.gui;

import jdraw.data.ColourEntry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

import util.Assert;
import util.Log;
import util.gui.AntialiasPanel;
import util.gui.FontDialog;

/*
 * GradientPanel.java - created on 26.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class GradientPanel extends AntialiasPanel {

	private static final int NORTH = 0;
	private static final int NORTH_HALF = 1;

	private static final int WEST = 10;
	private static final int WEST_HALF = 11;

	private static final int NORTH_EAST = 20;
	private static final int NORTH_EAST_HALF = 21;

	private static final int NORTH_WEST = 30;
	private static final int NORTH_WEST_HALF = 31;

	private static final int CENTER = 40;

	public static final GradientPanel INSTANCE = new GradientPanel();

	private GradientTile selectedTile;

	private final ButtonGroup gradientGroup = new ButtonGroup();
	private boolean cycle = false;
	private boolean invert = false;

	private final JPanel firstColour;
	private final JPanel secondColour;
	private final MListener mListener = new MListener();

	private GradientPanel() {
		super(new BorderLayout(0, 0));

		firstColour = createColourPanel(Color.white);
		secondColour = createColourPanel(Color.black);

		add(firstColour, BorderLayout.WEST);
		add(secondColour, BorderLayout.EAST);

		JPanel p = new JPanel(new GridLayout(0, 4, 1, 1));
		GradientTile tile = new GradientTile(NORTH);
		tile.setSelected(true);
		selectedTile = tile;
		p.add(tile);

		p.add(new GradientTile(WEST));
		p.add(new GradientTile(NORTH_WEST));
		p.add(new GradientTile(NORTH_EAST));

		p.add(new GradientTile(NORTH_HALF));
		p.add(new GradientTile(WEST_HALF));
		p.add(new GradientTile(NORTH_WEST_HALF));
		p.add(new GradientTile(NORTH_EAST_HALF));

		p.setBorder(new EmptyBorder(0, 2, 0, 2));
		add(p, BorderLayout.CENTER);

		JPanel boxPanel = new AntialiasPanel(new BorderLayout(0, 0));
		JCheckBox box = new JCheckBox("Cycle");
		box.setFont(MainFrame.DEFAULT_FONT);
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cycle = !cycle;
				repaint();
			}
		});
		boxPanel.add(box, BorderLayout.WEST);

		JCheckBox box2 = new JCheckBox("Invert");
		box2.setHorizontalAlignment(SwingConstants.RIGHT);
		box2.setFont(MainFrame.DEFAULT_FONT);
		box2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				invert = !invert;
				repaint();
			}
		});
		boxPanel.add(box2, BorderLayout.EAST);
		boxPanel.setBorder(new EmptyBorder(0, 2, 0, 2));
		add(boxPanel, BorderLayout.SOUTH);

		setBorder(new TitledBorder("Gradient Fill"));
	}

	private JPanel createColourPanel(Color col) {
		JPanel p = new JPanel();
		p.setBackground(col);
		p.setPreferredSize(ColourPanel.DIMENSION);
		p.setBorder(ColourPanel.PLAIN_BORDER);
		p.addMouseListener(mListener);

		return p;
	}

	private GradientTile createColourPanel(int direction) {
		GradientTile p = new GradientTile(direction);
		p.setPreferredSize(ColourPanel.DIMENSION);
		p.setBorder(ColourPanel.PLAIN_BORDER);
		p.addMouseListener(mListener);

		return p;
	}

	public final GradientPaint createGradient(Dimension d) {
		return createGradient(d, selectedTile.direction);
	}

	public final GradientPaint createGradient(Rectangle r) {
		return createGradient(r, selectedTile.direction);
	}

	private final GradientPaint createGradient(Rectangle r, int direction) {
		Point p1, p2;
		Color c1, c2;
		final Color first = firstColour.getBackground();
		final Color second = secondColour.getBackground();

		switch (direction) {
			case NORTH :
				p1 = new Point(r.x, r.y);
				p2 = new Point(r.x, r.y + r.height - 1);
				break;
			case NORTH_HALF :
				p1 = new Point(r.x, r.y);
				p2 = new Point(r.x, r.y + (r.height / 2));
				break;
			case WEST :
				p1 = new Point(r.x, r.y + (r.height / 2));
				p2 = new Point(r.x + r.width - 1, r.y + (r.height / 2));
				break;
			case WEST_HALF :
				p1 = new Point(r.x, r.y + (r.height / 2));
				p2 = new Point(r.x + (r.width / 2), r.y + (r.height / 2));
				break;
			case NORTH_WEST :
				p1 = new Point(r.x, r.y);
				p2 = new Point(r.x + r.width - 1, r.y + r.height - 1);
				break;
			case NORTH_WEST_HALF :
				p1 = new Point(r.x, r.y);
				p2 = new Point(r.x + (r.width / 2), r.y + (r.height / 2));
				break;
			case NORTH_EAST :
				p1 = new Point(r.x + r.width - 1, r.y);
				p2 = new Point(r.x, r.y + r.height - 1);
				break;
			case NORTH_EAST_HALF :
				p1 = new Point(r.x + r.width - 1, r.y);
				p2 = new Point(r.x + (r.width / 2), r.y + (r.height / 2));
				break;
			default :
				Assert.fail("gui: invalid direction " + direction);
				return null;
		}
		c1 = invert ? second : first;
		c2 = invert ? first : second;
		return new GradientPaint(p1, c1, p2, c2, cycle);
	}

	private final GradientPaint createGradient(Dimension d, int direction) {
		return createGradient(new Rectangle(0, 0, d.width, d.height), direction);
	}

	private final class MListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JPanel source = (JPanel) e.getSource();
				ColourEditor edit = ColourEditor.INSTANCE;
				edit.setColour(source.getBackground());
				edit.open();
				if (edit.getResult() == ColourEditor.APPROVE) {
					source.setBackground(edit.getColour());
					repaint();
				}
			}
		}
	}

	private final class GradientTile
		extends JToggleButton
		implements ActionListener {
		final int direction;

		public GradientTile(int aDirection) {
			gradientGroup.add(this);
			direction = aDirection;
			Dimension dim = ColourPanel.DIMENSION;
			setPreferredSize(dim);
			addActionListener(this);
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Dimension dim = getSize();
			g2.setPaint(createGradient(dim, direction));
			g2.fillRect(0, 0, dim.width, dim.height);
			if (getBorder() != null) {
				super.paintBorder(g);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (isSelected()) {
				selectedTile = this;
				Log.debug("selected: " + this);
			}
		}

	}
}
