package jdraw.gui;

import jdraw.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import util.ResourceLoader;
import util.gui.TextCalculator;

/*
 * AboutDialog.java - created on 16.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class AboutDialog extends DrawDialog {

	public AboutDialog() {
		super("About " + Main.APP_NAME);

		setModal(true);
		setUndecorated(false);
		JPanel p = new JPanel(new GridBagLayout());

		JLabel logo =
			new JLabel(ResourceLoader.getImage("jdraw/images/jdomain_logo.png"));

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;

		// überschrift		
		JLabel header = new JLabel(Main.APP_NAME);
		header.setFont(MainFrame.BIG_FONT);
		header.setBorder(new EmptyBorder(0, 0, 0, 60));
		gc.anchor = GridBagConstraints.SOUTHWEST;
		p.add(header, gc);

		gc.gridx++;
		// logo
		gc.anchor = GridBagConstraints.EAST;
		p.add(logo, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.anchor = GridBagConstraints.WEST;
		JLabel version = new JLabel("Version " + Main.VERSION.substring(1));
		version.setFont(MainFrame.BOLD_FONT);
		p.add(version, gc);

		gc.gridwidth = 2;
		gc.gridy++;

		String message =
			Main.APP_NAME
				+ " is an Open Source project. Feel free to distribute it.\n\n"
				+ "It's completely written in Java (v1.3, v1.4) and was successfully tested on "
				+ "Windows XP and SuSe Linux 8.1\n\n"
				+ Main.APP_NAME
				+ " was written and is maintained by J-Domain. If you're "
				+ "interested in further information about J-Domain, have a look "
				+ "at "
				+ Main.WWW
				+ ".\n\n"
				+ Main.APP_NAME
				+ " is a registered SourceForge project. For new versions "
				+ "and source code please refer to the following sites:\n\n"
				+ "\t<font color=blue>"
				+ Main.WWW_JDRAW
				+ "</font>\n"
				+ "\t<font color=blue>"
				+ Main.SF_WWW_JDRAW
				+ "</font>";

		TextCalculator calc =
			new TextCalculator(450, message, MainFrame.DEFAULT_FONT, true);

		JLabel textLabel = calc.createLabel();
		textLabel.setFont(MainFrame.DEFAULT_FONT);
		textLabel.setBorder(new EmptyBorder(20, 20, 0, 0));
		p.add(textLabel, gc);

		gc.gridx = 0;
		gc.gridy++;
		JLabel info =
			new JLabel(
				"<html>"
					+ Main.APP_NAME
					+ " "
					+ Main.VERSION
					+ " - "
					+ Main.EMAIL
					+ " - "
					+ Main.WWW
					+ "</html>");
		info.setBorder(new EmptyBorder(30, 0, 0, 0));
		info.setFont(MainFrame.TINY_FONT);
		p.add(info, gc);

		main.add(p, BorderLayout.CENTER);
		setDefaultBorder();
		addRightButton(getApproveButton());
		getApproveButton().setText("Close");
		getRootPane().setDefaultButton(getApproveButton());
		addButtonPanel();
	}
}
