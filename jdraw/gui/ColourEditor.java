package jdraw.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jdraw.data.ColourEntry;
import util.Util;
import util.gui.UpperCaseField;

/*
 * ColourEditor.java - created on 31.10.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class ColourEditor extends DrawDialog {

	public static final ColourEditor INSTANCE = new ColourEditor();

	private final JColorChooser chooser = new JColorChooser();
	private final PreviewPanel previewPanel = new PreviewPanel();

	private ColourEditor() {
		super("Colour Editor");
		setDisposeOnClose(false);
		setModal(true);

		chooser.setDragEnabled(true);
		chooser.addChooserPanel(new HexChooserPanel());
		chooser.addChooserPanel(new AlphaChooserPanel());
		chooser.getSelectionModel().addChangeListener(previewPanel);
		chooser.setPreviewPanel(previewPanel);
		main.add(chooser, BorderLayout.CENTER);

		setDefaultBorder();
		addRightButton(getApproveButton());
		addRightButton(getCancelButton());
		getRootPane().setDefaultButton(getApproveButton());
		addButtonPanel();
	}

	public void setColour(Color col) {
		chooser.setColor(col);
		previewPanel.setColour(col);
	}

	public Color getColour() {
		return chooser.getColor();
	}

	private final class PreviewPanel extends JPanel implements ChangeListener {

		private final JPanel colPanel = new JPanel();
		private final JLabel hexLabel = new JLabel(" ");
		private final JLabel decLabel = new JLabel(" ");
		private final JLabel alphaLabel = new JLabel(" ");

		public PreviewPanel() {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));

			JPanel p = new JPanel(new GridBagLayout());
			add(p);
			p.setBorder(new EmptyBorder(10, 0, 0, 0));
			colPanel.setPreferredSize(new Dimension(50, 10));
			colPanel.setBorder(new EtchedBorder());
			hexLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

			GridBagConstraints gc = new GridBagConstraints();
			gc.insets = new Insets(0, 0, 0, 10);
			gc.fill = GridBagConstraints.BOTH;
			gc.anchor = GridBagConstraints.WEST;
			gc.gridx = 0;
			gc.gridy = 0;

			gc.gridheight = 2;
			p.add(colPanel, gc);
			gc.gridx++;
			gc.gridheight = 1;
			p.add(decLabel, gc);
			gc.gridx = 1;
			gc.gridy++;
			p.add(hexLabel, gc);
			gc.gridx++;
		}

		public void setColour(Color col) {
			colPanel.setBackground(col);
			int r = col.getRed();
			int g = col.getGreen();
			int b = col.getBlue();
			int alpha = col.getAlpha();
			hexLabel.setText(
				"<html><b>hex</b>("
					+ Util.hexString(r, 2)
					+ ","
					+ Util.hexString(g, 2)
					+ ","
					+ Util.hexString(b, 2)
					+ ","
					+ Util.hexString(alpha, 2)
					+ ")</html>");
			hexLabel.setFont(MainFrame.DEFAULT_FONT);
			decLabel.setText(
				"<html><b>rgb</b>("
					+ r
					+ ","
					+ g
					+ ","
					+ b
					+ ","
					+ alpha
					+ ")</html>");
			decLabel.setFont(MainFrame.DEFAULT_FONT);
		}

		public void stateChanged(ChangeEvent e) {
			setColour(chooser.getColor());
		}

	}

	private final class HexChooserPanel
		extends AbstractColorChooserPanel
		implements DocumentListener {
		private final JTextField hexField = new UpperCaseField(8);

		public HexChooserPanel() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
			add(new JLabel(getDisplayName() + ": #"));
			add(hexField);
			hexField.getDocument().addDocumentListener(this);
		}

		protected void buildChooser() {
		}

		public String getDisplayName() {
			return "Hex";
		}

		public Icon getLargeDisplayIcon() {
			return null;
		}

		public Icon getSmallDisplayIcon() {
			return null;
		}

		public void updateChooser() {
			if (INSTANCE == null) {
				return;
			}

			if (!hexField.hasFocus()) {
				Color col = INSTANCE.chooser.getColor();
				String s =
					Util.hexString(col.getRed(), 2)
						+ Util.hexString(col.getGreen(), 2)
						+ Util.hexString(col.getBlue(), 2)
						+ Util.hexString(col.getAlpha(), 2);
				hexField.setText(s.toUpperCase());
			}
		}

		public int getMnemonic() {
			return KeyEvent.VK_X;
		}

		public int getDisplayedMnemonicIndex() {
			return 2;
		}

		private void update() {
			String s = hexField.getText().trim();

			if (Util.isMinMax(s.length(), 6, 8)) {
				String r = s.substring(0, 2);
				String g = s.substring(2, 4);
				String b = s.substring(4, 6);

				String a = "";
				if (s.length() > 6) {
					a = s.substring(6);
					if (a.length() == 1) {
						a = a + '0';
					}
				}
				if (Util.isNumber(r, 16, 0, ColourEntry.MAX_VALUE)
					&& Util.isNumber(g, 16, 0, ColourEntry.MAX_VALUE)
					&& Util.isNumber(b, 16, 0, ColourEntry.MAX_VALUE)
					&& Util.isNumber(a, 16, 0, ColourEntry.MAX_VALUE)) {
					INSTANCE.chooser.setColor(
						new Color(
							Util.asInt(r, 16),
							Util.asInt(g, 16),
							Util.asInt(b, 16),
							Util.asInt(a, 16)));
				}
			}
		}

		public void changedUpdate(DocumentEvent e) {
			update();
		}

		public void insertUpdate(DocumentEvent e) {
			update();
		}

		public void removeUpdate(DocumentEvent e) {
			update();
		}
	}

	private final class AlphaChooserPanel
		extends AbstractColorChooserPanel
		implements DocumentListener, ChangeListener {
		private final JTextField alphaField = new JTextField(3);
		private final JSlider alphaSlider =
			new JSlider(JSlider.VERTICAL, 0, ColourEntry.MAX_VALUE, 0);

		public AlphaChooserPanel() {
			setLayout(new GridBagLayout());
			alphaSlider.addChangeListener(this);
			Hashtable map = new Hashtable();
			alphaSlider.setMajorTickSpacing(20);
			alphaSlider.setMinorTickSpacing(5);
			alphaSlider.setPaintTicks(true);
			map.put(new Integer(0), createLabel("transparent", 0));

			int count = 50;
			while (count < ColourEntry.MAX_VALUE - 50) {
				map.put(new Integer(count), createLabel(null, count));
				count = count + 50;
			}
			map.put(new Integer(255), createLabel("opaque", 255));
			alphaSlider.setLabelTable(map);
			alphaSlider.setPaintLabels(true);

			alphaField.getDocument().addDocumentListener(this);

			GridBagConstraints gc = new GridBagConstraints();
			gc.gridx = 0;
			gc.gridy = 0;
			gc.anchor = GridBagConstraints.WEST;
			gc.insets = new Insets(0, 0, 0, 10);

			add(new JLabel(getDisplayName()), gc);
			gc.gridx++;
			add(alphaSlider, gc);
			gc.gridx++;
			add(alphaField, gc);
		}

		private JLabel createLabel(String text, int value) {
			JLabel label = new JLabel();
			label.setFont(MainFrame.DEFAULT_FONT);
			if (text == null) {
				label.setText(String.valueOf(value));
			}
			else {
				label.setText(text);
			}
			return label;
		}

		protected void buildChooser() {
		}

		public String getDisplayName() {
			return "Alpha";
		}

		public Icon getLargeDisplayIcon() {
			return null;
		}

		public Icon getSmallDisplayIcon() {
			return null;
		}

		public void updateChooser() {
			if (INSTANCE == null) {
				return;
			}

			int alpha = INSTANCE.chooser.getColor().getAlpha();
			if (!alphaField.hasFocus()) {
				alphaField.setText(String.valueOf(alpha));
			}
			if (!alphaSlider.hasFocus()) {
				alphaSlider.setValue(alpha);
			}
		}

		public int getMnemonic() {
			return KeyEvent.VK_A;
		}

		public int getDisplayedMnemonicIndex() {
			return 2;
		}

		private void update() {
			String s = alphaField.getText().trim();
			if (Util.isNumber(s, 0, 255)) {
				int alpha = Util.asInt(s);
				Color col = INSTANCE.chooser.getColor();
				col = new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
				INSTANCE.setColour(col);
			}
		}

		public void changedUpdate(DocumentEvent e) {
			update();
		}

		public void insertUpdate(DocumentEvent e) {
			update();
		}

		public void removeUpdate(DocumentEvent e) {
			update();
		}

		public void stateChanged(ChangeEvent e) {
			Color col = INSTANCE.getColour();
			col =
				new Color(
					col.getRed(),
					col.getGreen(),
					col.getBlue(),
					alphaSlider.getValue());
			INSTANCE.setColour(col);
		}

	}

}
