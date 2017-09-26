package jdraw.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import util.ResourceLoader;

/*
 * QuestionDialog.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public final class QuestionDialog extends DrawDialog {

	public QuestionDialog(String title, String question) {
		super(title);
		setModal(true);
		setDefaultBorder();
		JPanel p = new JPanel(new BorderLayout(0, 0));
		JLabel iconLabel =
			new JLabel(ResourceLoader.getImage("jdraw/images/help.png"));
		iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
		p.add(iconLabel, BorderLayout.WEST);

		//p.setBorder(new EmptyBorder(10, 10, 10, 10));
		JLabel l = new JLabel(question);
		p.add(l, BorderLayout.CENTER);
		l.setHorizontalAlignment(SwingConstants.LEFT);

		main.add(p, BorderLayout.CENTER);

		addRightButton(getApproveButton());
		getApproveButton().setText("Yes");
		getRootPane().setDefaultButton(getApproveButton());

		addRightButton(getCancelButton());
		getCancelButton().setText("No");

		addButtonPanel();
	}
}
