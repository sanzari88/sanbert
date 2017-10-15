package jdraw.gui;

import jdraw.data.Picture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;

import Inizio.Balza;
import util.gui.WidgetFactory;

/*
 * SizeDialog.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class NewBalzaDialog extends DrawDialog implements DocumentListener {

	private static final int MAX_WIDTH = 1024;
	private static final int MAX_HEIGHT = 768;

	
	private final JComboBox costina;


	public NewBalzaDialog() {
		super("Seleziona tipo balza:");
		
		String[] tipoCosta = new String[] {"Costa 1x1", "Costa 2x1",
	            "Tubolare","Costa 1x1 + elastico", "Costa 2x1 + elastico"};
		ArrayList<Balza> balze=readCostineFromFile();
		
		costina=new JComboBox(balze.toArray());
		

		WidgetFactory.addFocusAdapterJComboBox(costina);
		setDefaultBorder();
		setModal(true);
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		//costina.getDocument().addDocumentListener(this);
		//costina.add("");
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 2, 2, 2);
		gc.gridwidth = 2;
		JLabel text = new JLabel("<html><b>Tipo balza:</b></html>");
		text.setBorder(new EmptyBorder(0, 0, 10, 0));
		p.add(text, gc);

		gc.gridwidth = 1;
		gc.gridy++;
		JLabel l = new JLabel("Balza/Inizio: ");
		p.add(l, gc);

		gc.gridx++;
		p.add(costina, gc);

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

		setFirstFocusComponent(costina);
	}
	
	private ArrayList<Balza> readCostineFromFile() {
		
		File folder = new File("Balze");
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<Balza> balze = new ArrayList<>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
		    balze.add(new Balza(file.getName(),file));
		}
		return balze;
		
	}
	
	public Balza getCostina() {
		Balza c=(Balza) costina.getSelectedItem();
		return c;
	}


	private void checkInput() {
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
