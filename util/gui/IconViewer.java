package util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import util.ResourceLoader;

public final class IconViewer extends StandardScrollPane implements ListDataListener {

	private static final int LENGTH = 50;
	private static final HashMap ICON_MAP = new HashMap();

	private static final Border UNSELECTED_BORDER =
		new LineBorder(Color.lightGray);
	private static final Border SELECTED_BORDER = new LineBorder(Color.blue);

	private static final String[] JDK_SUPPORTED_IMAGE_EXTENSIONS =
		{ ".bmp", ".gif", ".jpg", ".jpeg", ".png", ".pnm", ".tif" };

	private static final ArrayList IMAGE_HANDLER = new ArrayList();

	private static final BrowserFilter ALL_IMAGES_FILTER =
		BrowserFilter.createFilter("<hidden>", JDK_SUPPORTED_IMAGE_EXTENSIONS);

	public static final boolean isImage(File file) {
		if (file.isDirectory()) {
			return false;
		}
		Iterator it = IMAGE_HANDLER.iterator();
		String fileName = file.toString();
		while (it.hasNext()) {
			if (((ImageHandler) it.next()).canHandleImage(fileName)) {
				return true;
			}
		}
		return ALL_IMAGES_FILTER.accept(file);
	}

	public static final boolean isImage(String fileName) {
		return isImage(new File(fileName));
	}

	private GridBagConstraints gc = new GridBagConstraints();
	private JPanel iconPanel =
		new ScrollablePanel(new GridBagLayout(), LENGTH, 5);

	private PropertyChangeEvent lastChangeEvent;
	private final ListModel model;

	private MouseAdapter mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if ((e.getModifiers() == MouseEvent.BUTTON1_MASK)) {
				IconLabel source = (IconLabel) e.getSource();
				File f = new File(source.fileName);
				FileBrowser browser =
					(FileBrowser) GUIUtil.findParentComponentOfClass(
						source,
						FileBrowser.class);

				if (browser != null) {
					browser.setSelectedFile(f);
				}
			}
		}
	};

	public IconViewer(ListModel aModel) {
		model = aModel;
		iconPanel.setBackground(Color.white);
		setEnsuredHeight(LENGTH + 4);
		setPreferredSize(new Dimension(10, LENGTH + 4));
		setViewportView(iconPanel);
		getViewport().setBackground(Color.white);
		setBorder(new CompoundBorder(new EmptyBorder(10, 0, 0, 0), getBorder()));
		gc.fill = GridBagConstraints.VERTICAL;
		gc.insets = new Insets(1, 1, 1, 1);
		gc.anchor = GridBagConstraints.CENTER;
		gc.ipadx = 10;

		model.addListDataListener(this);
	}

	public static void addImageHandler(ImageHandler handler) {
		IMAGE_HANDLER.add(handler);
	}

	private void addImage(IconLabel icon) {
		iconPanel.add(icon, gc);
		gc.gridx++;
	}

	private ImageIcon getHandlerIcon(String fileName) {
		Iterator it = IMAGE_HANDLER.iterator();
		ImageHandler handler;
		while (it.hasNext()) {
			handler = (ImageHandler) it.next();
			if (handler.canHandleImage(fileName)) {
				return handler.createIconLabel(fileName);
			}
		}
		return null;
	}

	private void addImage(String fileName) {
		ImageIcon icon = getHandlerIcon(fileName);
		if (icon == null) {
			icon = new ImageIcon(fileName);
		}
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		icon = ResourceLoader.scaleImage(icon, LENGTH);
		IconLabel label = new IconLabel(icon, fileName, w, h);
		label.setBorder(UNSELECTED_BORDER);
		addImage(label);
		ICON_MAP.put(fileName, label);
	}

	public void contentsChanged(ListDataEvent e) {
		rebuild();
	}

	public void intervalAdded(ListDataEvent e) {
		rebuild();
	}

	public void intervalRemoved(ListDataEvent e) {
		rebuild();
	}

	protected void rebuild() {
		iconPanel.setVisible(false);
		gc.gridx = 0;
		gc.gridy = 0;
		iconPanel.removeAll();
		ICON_MAP.clear();
		File file;
		for (int i = 0; i < model.getSize(); i++) {
			file = new File(model.getElementAt(i).toString());
			if (isImage(file)) {
				addImage(file.toString());
			}
		}
		iconPanel.setVisible(true);
	}

	protected void updateSelection() {
		if (lastChangeEvent != null) {
			selectionChanged(lastChangeEvent);
		}
	}

	protected void selectionChanged(PropertyChangeEvent e) {
		lastChangeEvent = e;
		if (getParent() != null) { // preview is shown
			changeSelection((File) e.getOldValue(), false);
			changeSelection((File) e.getNewValue(), true);
		}
	}

	private void changeSelection(File file, boolean doSelect) {
		if (file == null) {
			return;
		}
		String fileName = file.toString();
		IconLabel label = (IconLabel) ICON_MAP.get(fileName);
		if (label != null) {

			label.setBorder(doSelect ? SELECTED_BORDER : UNSELECTED_BORDER);
			if (doSelect) { // scroll to selected label
				if (!isVisible(label)) {
					int maxX =
						iconPanel.getWidth() - getViewport().getExtentSize().width;
					if (maxX <= 0) {
						maxX = 1;
					}
					int x = Math.min(maxX, label.getLocation().x) - 1;
					getViewport().setViewPosition(new Point(x, 0));
				}
			}
		}
	}

	private boolean isVisible(IconLabel label) {
		int x = label.getLocation().x - 1;
		int w = label.getWidth() + 1;
		Rectangle r = getViewport().getViewRect();
		return (x >= r.x) && (x + w <= r.x + r.width);
	}

	// Icon Label
	public final class IconLabel extends JLabel {
		public final String fileName;

		public IconLabel(Icon icon, String aFileName, int w, int h) {
			super(icon);
			this.setOpaque(false);
			setToolTipText("" + w + "x" + h);
			fileName = aFileName;
			IconLabel.this.addMouseListener(mouseListener);
		}
	}

	public static interface ImageHandler {
		public ImageIcon createIconLabel(String fileName);
		public boolean canHandleImage(String fileName);
	}

}
