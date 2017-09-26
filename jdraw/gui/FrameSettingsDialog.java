package jdraw.gui;

import jdraw.Main;
import jdraw.data.Frame;
import jdraw.data.FrameSettings;
import jdraw.data.Picture;
import jdraw.gui.dnd.DnDUtil;
import jdraw.gui.dnd.FrameDragger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import util.Assert;
import util.Log;
import util.ResourceLoader;
import util.Util;
import util.gui.WidgetFactory;

/*
 * SizeDialog.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public class FrameSettingsDialog
	extends DrawDialog
	implements DocumentListener, ListSelectionListener, ActionListener, ListDataListener {

	private static final Border CELL_BORDER = new EmptyBorder(2, 6, 2, 6);

	private Picture picture;
	private FrameEntry copiedEntry = null;

	private static final String[] DISPOSAL_TYPES =
		{
			"Don't care",
			"Do not dispose",
			"Restore to background",
			"Restore previous image" };

	public static final ImageIcon FRAME_ICON =
		ResourceLoader.getImage("jdraw/images/frame.png", 20);

	private final JButton applyButton = new JButton("Apply");

	private boolean hasChanges = false;

	private final JButton removeButton = new JButton("Remove Frame");
	private final JButton copyButton = new JButton("Copy Frame");
	private final JButton pasteButton = new JButton("Paste Frame");

	private int newFrameCount = 0;
	private final JPanel settingsPanel;
	private final JList frameList;
	private final JTextField delayField = new JTextField();
	private final JComboBox disposalBox = new JComboBox(DISPOSAL_TYPES);
	private final JTextField iconWidthField = new JTextField();
	private final JTextField iconHeightField = new JTextField();

	public FrameSettingsDialog() {
		super("Frame Settings");
		setModal(true);	
		setUndecorated(false);	
		setResizable(true);

		WidgetFactory.addFocusAdapter(delayField);
		frameList = new JList(createFrameList());
		frameList.getModel().addListDataListener(this);
		frameList.setVisibleRowCount(10);
		frameList.setCellRenderer(new FrameEntryRenderer());
		frameList.addListSelectionListener(this);
		frameList.setFixedCellHeight(24);
		frameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane pane = new JScrollPane(frameList);
		Dimension dim = pane.getPreferredSize();
		dim.width = dim.width + 10;
		pane.setPreferredSize(dim);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		main.add(pane, BorderLayout.CENTER);

		delayField.getDocument().addDocumentListener(this);
		disposalBox.addActionListener(this);
		iconWidthField.getDocument().addDocumentListener(this);
		iconHeightField.getDocument().addDocumentListener(this);

		settingsPanel = createSettingsPanel();
		JPanel east = new JPanel(new BorderLayout());
		east.add(settingsPanel, BorderLayout.NORTH);
		main.add(east, BorderLayout.EAST);

		addLeftButton(copyButton);
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyFrame();
			}
		});
		copyButton.setMnemonic('f');

		addLeftButton(pasteButton);
		pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pasteFrame();
			}
		});
		pasteButton.setMnemonic('p');

		addLeftButton(removeButton);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFrame();
			}
		});
		removeButton.setMnemonic('r');

		addRightButton(getApproveButton());
		getRootPane().setDefaultButton(getApproveButton());

		addRightButton(applyButton);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		applyButton.setMnemonic('a');

		addRightButton(getCancelButton());
		addButtonPanel();
		setDefaultBorder();

		// init
		new FrameDragger(frameList);
		frameList.setSelectedIndex(Tool.getPicture().getCurrentFrameIndex());
		hasChanges = false;
		checkInput();
	}

	protected final void approve() {
		apply();
		super.approve();
	}

	private final void apply() {
		Picture orig = Tool.getPicture();
		picture = new Picture(orig.getWidth(), orig.getHeight());
		picture.setPalette(orig.getPalette());

		ListModel model = frameList.getModel();
		final int entries = model.getSize();
		FrameEntry entry;
		for (int i = 0; i < entries; i++) {
			entry = (FrameEntry) model.getElementAt(i);
			picture.addFrame(entry.produceFrame());
		}
		picture.setCurrentFrame(0);
		if (orig.getCurrentFrameIndex() < picture.getFrameCount()) {
			picture.setCurrentFrame(orig.getCurrentFrameIndex());
		}
		picture.setTransparent(orig.getTransparent());
		Main.setPicture(picture);
		hasChanges = false;
		checkInput();
	}

	private final void removeFrame() {
		final int selIndex = frameList.getSelectedIndex();
		((DefaultListModel) frameList.getModel()).remove(selIndex);
		frameList.clearSelection();
		hasChanges = true;
		checkInput();
	}

	private JPanel createSettingsPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		gc.insets = new Insets(0, 0, 4, 6);
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = 0;

		JLabel title = new JLabel("Delay in 1/100 seconds:");
		p.add(title, gc);

		gc.gridy++;
		p.add(delayField, gc);

		gc.gridy++;
		title = new JLabel("Disposal method:");
		p.add(title, gc);

		gc.gridy++;
		p.add(disposalBox, gc);

		gc.gridy++;
		title = new JLabel("Icon width: (1-" + Tool.getPictureWidth() + ")");
		title.setBorder(new EmptyBorder(15, 0, 0, 0));
		p.add(title, gc);

		gc.gridy++;
		p.add(iconWidthField, gc);

		gc.gridy++;
		title = new JLabel("Icon height: (1-" + Tool.getPictureHeight() + ")");
		p.add(title, gc);

		gc.gridy++;
		p.add(iconHeightField, gc);

		p.setBorder(new EmptyBorder(0, 10, 0, 10));

		return p;
	}

	private void checkInput() {
		picture = null;
		final int selIndex = frameList.getSelectedIndex();
		boolean gotSelection = (selIndex != -1);
		removeButton.setEnabled(
			gotSelection && frameList.getModel().getSize() > 1);
		copyButton.setEnabled(gotSelection);
		pasteButton.setEnabled(copiedEntry != null);
		delayField.setEnabled(gotSelection);
		disposalBox.setEnabled(gotSelection);
		iconWidthField.setEnabled(gotSelection);
		iconHeightField.setEnabled(gotSelection);
		boolean isValid =
			(!gotSelection)
				|| (Util.isNumber(delayField.getText(), 0, Integer.MAX_VALUE)
					&& Util.isIn(getIconWidth(), 1, Tool.getPictureWidth())
					&& Util.isIn(getIconHeight(), 1, Tool.getPictureHeight()));
		applyButton.setEnabled(hasChanges && isValid);
		getApproveButton().setEnabled(hasChanges && isValid);
	}

	private final int getIconWidth() {
		String text = iconWidthField.getText().trim();
		return Util.asInt(text);
	}

	private final int getIconHeight() {
		String text = iconHeightField.getText().trim();
		return Util.asInt(text);
	}

	private ListModel createFrameList() {
		Picture pic = Tool.getPicture();
		final int frames = pic.getFrameCount();

		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < frames; i++) {
			model.addElement(new FrameEntry(pic.getFrame(i)));
		}
		return model;
	}

	public void actionPerformed(ActionEvent e) {
		if (disposalBox.isEnabled()) {
			FrameEntry entry = (FrameEntry) frameList.getSelectedValue();
			if (entry != null) {
				entry.disposalMethod = disposalBox.getSelectedIndex();
			}
			hasChanges = true;
			checkInput();
		}
	}

	private void documentUpdate(Document doc) {
		FrameEntry entry = (FrameEntry) frameList.getSelectedValue();

		if (doc == delayField.getDocument()) {
			entry.delay = delayField.getText().trim();
		}
		else if (doc == iconWidthField.getDocument()) {
			entry.iconWidth = iconWidthField.getText().trim();
		}
		else if (doc == iconHeightField.getDocument()) {
			entry.iconHeight = iconHeightField.getText().trim();
		}
		hasChanges = true;
		checkInput();
	}

	public void insertUpdate(DocumentEvent e) {
		documentUpdate(e.getDocument());
	}

	public void removeUpdate(DocumentEvent e) {
		documentUpdate(e.getDocument());
	}

	public void changedUpdate(DocumentEvent e) {
		documentUpdate(e.getDocument());
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( e.getValueIsAdjusting() ) {
			return;
		}
		final int index = frameList.getSelectedIndex();
		if (index == -1) {
			delayField.setText("");
			iconWidthField.setText("");
			iconHeightField.setText("");
			disposalBox.setSelectedIndex(0);
		}
		else {
			FrameEntry entry = (FrameEntry) frameList.getSelectedValue();
			delayField.setText(entry.delay);
			iconWidthField.setText(entry.iconWidth);
			iconHeightField.setText(entry.iconHeight);
			disposalBox.setSelectedIndex(entry.disposalMethod);
		}
		checkInput();
	}

	public void contentsChanged(ListDataEvent e) {
		hasChanges = true;
		checkInput();
	}

	public void intervalAdded(ListDataEvent e) {
		hasChanges = true;
		frameList.setSelectedIndex(e.getIndex0());
		checkInput();
	}

	public void intervalRemoved(ListDataEvent e) {
		hasChanges = true;
		frameList.setSelectedIndex(0);
		checkInput();
	}

	private void copyFrame() {
		final int selIndex = frameList.getSelectedIndex();
		if (selIndex != -1) {
			copiedEntry = ((FrameEntry) frameList.getSelectedValue()).createCopy();
			Log.info("Frame copied.");
			checkInput();
		}
	}

	private void pasteFrame() {
		if (copiedEntry != null) {
			int index = frameList.getSelectedIndex();
			if (index == -1) {
				index = frameList.getModel().getSize();
			}
			index++;
			if (index >= frameList.getModel().getSize()) {
				((DefaultListModel) frameList.getModel()).addElement(copiedEntry);
			}
			else {
				((DefaultListModel) frameList.getModel()).insertElementAt(
					copiedEntry,
					index);
			}
			Log.info("Frame pasted.");
		}
	}

	public final boolean handleKey(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_TYPED) {
			if (e.getModifiers() == KeyEvent.CTRL_MASK) {
				if (e.getKeyChar() == 0x03) { // Ctrl-C 
					copyFrame();
				}
				else if (e.getKeyChar() == 0x16) { // Ctrl-V
					pasteFrame();
				}
			}
			if ((e.getKeyChar() == 0x1b) && (e.getModifiers() == 0)) { // ESC
				cancel();
				return true;
			}
		}
		return false;
	}

	// Frame Entry
	public final class FrameEntry implements Transferable, Serializable {

		private String name;
		private Frame frame;
		public String delay = "";
		public int disposalMethod = 0;
		private Frame copiedFrame = null;
		private String iconWidth = "";
		private String iconHeight = "";

		private FrameEntry() {
		}

		public FrameEntry(Frame aFrame) {
			frame = aFrame;
			if (frame == null) {
				newFrameCount++;
				name = "New Frame #" + newFrameCount;
			}
			else {
				name = "Frame #" + (Tool.getPicture().indexOf(frame) + 1);
				final FrameSettings fset = frame.getSettings();
				delay = String.valueOf(fset.getDelay());
				disposalMethod = fset.getDisposalMethod();
				iconWidth = String.valueOf(fset.getIconWidth());
				iconHeight = String.valueOf(fset.getIconHeight());
			}
		}

		public Frame produceFrame() {
			Frame f;
			if (frame != null) {
				f = frame;
			}
			else {
				f = copiedFrame.copy(picture);
			}
			final FrameSettings fset = f.getSettings();
			fset.setDelay(Integer.parseInt(delay));
			fset.setDisposalMethod(disposalMethod);
			fset.setIconWidth(Integer.parseInt(iconWidth));
			fset.setIconHeight(Integer.parseInt(iconHeight));
			return f;
		}

		public FrameEntry createCopy() {
			Frame f = frame;
			if (f == null) {
				f = copiedFrame;
			}
			Assert.notNull(f, "gui: internal error. frameEntry without frame");
			FrameEntry e = new FrameEntry();
			e.copiedFrame = f;
			e.name = "Copy of " + name;
			e.delay = delay;
			e.disposalMethod = disposalMethod;
			e.iconWidth = iconWidth;
			e.iconHeight = iconHeight;

			return e;
		}

		public FrameEntry createIdenticalCopy() {
			FrameEntry e = new FrameEntry();
			e.name = name;
			e.copiedFrame = copiedFrame;
			e.frame = frame;
			e.delay = delay;
			e.disposalMethod = disposalMethod;
			e.iconWidth = iconWidth;
			e.iconHeight = iconHeight;

			return e;
		}

		public String toString() {
			return name;
		}

		public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
			return this;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return DnDUtil.FRAME_FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor == DnDUtil.FRAME_FLAVORS[0];
		}

	}

	// Renderer

	private final class FrameEntryRenderer extends DefaultListCellRenderer {

		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
			JLabel label =
				(JLabel) super.getListCellRendererComponent(
					list,
					value,
					index,
					isSelected,
					cellHasFocus);
			label.setIcon(FRAME_ICON);
			label.setBorder(CELL_BORDER);
			return label;
		}
	}

}
