package jdraw.gui;

import jdraw.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import util.Assert;
import util.ResourceLoader;
import util.Util;
import util.gui.TextCalculator;
import util.gui.WidgetFactory;

/*
 * ScaleDialog.java - created on 11.12.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class ScaleDialog extends DrawDialog implements DocumentListener {

	private final JTextField widthField = new JTextField(6);
	private final JTextField heightField = new JTextField(6);
	private final JCheckBox keepRatioBox = new JCheckBox("Keep ratio");
	private boolean settingRatio = false;
	private final JComboBox styleBox =
		new JComboBox(
			new String[] { "Scale smoothly", "Scale average", "Scale fast" });

	public ScaleDialog() {
		super("Scaling Dialog");

		setModal(true);
		setUndecorated(true);

		widthField.addFocusListener(WidgetFactory.TEXTFIELD_FOCUS_ADAPTER);
		widthField.setText(String.valueOf(Tool.getPictureWidth()));
		widthField.getDocument().addDocumentListener(this);

		heightField.setText(String.valueOf(Tool.getPictureWidth()));
		heightField.getDocument().addDocumentListener(this);

		keepRatioBox.setSelected(true);

		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 2, 2, 2);

		JLabel label =
			new JLabel("<html><b>Please enter scaled image size:</b></html>");
		label.setBorder(new EmptyBorder(0, 0, 10, 0));
		p.add(label, gc);

		gc.gridwidth = 1;
		gc.gridy++;
		label = new JLabel("Scaled image width:");
		p.add(label, gc);

		gc.gridx++;
		p.add(widthField, gc);

		gc.gridx = 0;
		gc.gridy++;
		label = new JLabel("Scaled image height:");
		p.add(label, gc);

		gc.gridx++;
		p.add(heightField, gc);

		gc.gridy++;
		gc.gridx = 0;
		p.add(keepRatioBox, gc);

		gc.gridx++;
		p.add(styleBox, gc);

		main.add(p, BorderLayout.CENTER);
		setDefaultBorder();
		addRightButton(getApproveButton());
		getRootPane().setDefaultButton(getApproveButton());
		addRightButton(getCancelButton());
		addButtonPanel();
	}

	public int getScalingStyle() {
		final int i = styleBox.getSelectedIndex();
		switch (i) {
			case 0 :
				return Image.SCALE_SMOOTH;
			case 1 :
				return Image.SCALE_AREA_AVERAGING;
			case 2 :
				return Image.SCALE_FAST;
			default :
				Assert.fail("gui: unknown style " + i);
				return -1;
		}
	}

	public Dimension getScalingDimension() {
		return new Dimension(
			Util.asInt(widthField.getText().trim()),
			Util.asInt(heightField.getText().trim()));
	}

	private void checkInput() {
		String w = widthField.getText().trim();
		String h = heightField.getText().trim();
		if (Util.isNumber(w, 1, Integer.MAX_VALUE)
			&& Util.isNumber(h, 1, Integer.MAX_VALUE)) {
			getApproveButton().setEnabled(true);
		}
		else {
			getApproveButton().setEnabled(false);
		}
	}

	private void update(Document doc) {
		if (settingRatio) {
			return;
		}
		if (doc == widthField.getDocument()) {
			String width = widthField.getText().trim();
			if (Util.isNumber(width, 1, Integer.MAX_VALUE)) {
				double w = Util.asInt(width);
				if (keepRatioBox.isSelected()) {
					settingRatio = true;
					double ratio =
						((double) Tool.getPictureWidth())
							/ ((double) Tool.getPictureHeight());
					int height = (int) Math.round(w * ratio);
					heightField.setText(String.valueOf(height));
				}
			}
		}
		else { // height change
			String height = heightField.getText().trim();
			if (Util.isNumber(height)) {
				if (Util.isNumber(height, 1, Integer.MAX_VALUE)) {
					double h = Util.asInt(height);
					if (keepRatioBox.isSelected()) {
						settingRatio = true;
						double ratio =
							((double) Tool.getPictureHeight())
								/ ((double) Tool.getPictureWidth());
						int width = (int) Math.round(h * ratio);
						widthField.setText(String.valueOf(width));
					}
				}
			}
		}
		checkInput();
		settingRatio = false;
	}

	public void changedUpdate(DocumentEvent e) {
		update(e.getDocument());
	}

	public void insertUpdate(DocumentEvent e) {
		update(e.getDocument());
	}

	public void removeUpdate(DocumentEvent e) {
		update(e.getDocument());
	}
}
