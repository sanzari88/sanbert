package jdraw.gui;

import jdraw.data.Picture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.gui.WidgetFactory;

/*
 * SizeDialog.java - created on 11-10-2017
 * 
 * @author Sanzari Raffaele 
 */

public class VelocitaDialog extends DrawDialog implements DocumentListener {

	private static final int MAX_VEL = 20;
	//private static final int MAX_HEIGHT = 768;

	private Dimension dim = Tool.getPictureDimension();

	private final JTextField velocitaNUmber =new JTextField();

//	private final JTextField heightField =
//		new JTextField(String.valueOf(dim.height), 4);

	public VelocitaDialog() {
		super("Inserisci velocita:");

		WidgetFactory.addFocusAdapter(velocitaNUmber);
//		WidgetFactory.addFocusAdapter(heightField);
		setDefaultBorder();
		setModal(true);
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		velocitaNUmber.getDocument().addDocumentListener(this);
//		heightField.getDocument().addDocumentListener(this);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 2, 2, 2);
		gc.gridwidth = 2;
		JLabel text = new JLabel("<html><b>Seleziona Velocita:</b></html>");
		text.setBorder(new EmptyBorder(0, 0, 10, 0));
		p.add(text, gc);

		gc.gridwidth = 1;
		gc.gridy++;
		JLabel l = new JLabel("Velocita: ");
		p.add(l, gc);

		gc.gridx++;
		p.add(velocitaNUmber, gc);

//		gc.gridx = 0;
//		gc.gridy++;
//		l = new JLabel("Altezza: ");
//		p.add(l, gc);
//
//		gc.gridx++;
//		p.add(heightField, gc);
		
		main.add(p, BorderLayout.CENTER);

		addRightButton(getApproveButton());
		getRootPane().setDefaultButton(getApproveButton());

		addRightButton(getCancelButton());
		addButtonPanel();

		setFirstFocusComponent(velocitaNUmber);
	}

	public String getVelocita() {
		return velocitaNUmber.getText();
	}

	private void checkInput() {
		boolean isValid = true;
		try {
			dim.width = Integer.parseInt(velocitaNUmber.getText().trim());
			//dim.height = Integer.parseInt(heightField.getText().trim());

			isValid =(dim.width > 0)&& (dim.width < MAX_VEL);
		}
		catch (NumberFormatException e) {
			isValid = false;
		}
		getApproveButton().setEnabled(isValid);
	}

	public void changedUpdate(DocumentEvent e) {
		checkInput();
	}

	public void insertUpdate(DocumentEvent e) {
		checkInput();
	}

	public void removeUpdate(DocumentEvent e) {
		checkInput();
	}

}
