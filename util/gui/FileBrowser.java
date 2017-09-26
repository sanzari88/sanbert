package util.gui;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class FileBrowser
	extends JFileChooser
	implements WindowFocusListener, PropertyChangeListener {

	private boolean ignoreKeys = true;

	public static final BrowserFilter ALL_FILES_FILTER =
		new BrowserFilter("All Files", new String[] { ".*" }) {

		protected boolean doAccept(File f) {
			return true;
		}
	};

	private JDialog dialog;
	private String openPath = System.getProperty("user.dir");

	private boolean previewing = true;
	private String savePath = openPath;

	public FileBrowser() {
		setAcceptAllFileFilterUsed(false);
		addPropertyChangeListener(
			FileBrowser.SELECTED_FILE_CHANGED_PROPERTY,
			this);
	}

	/** wird aufgerufen, falls hasValidExtension false liefert.*/
	protected String adjustFilename(
		String filename,
		BrowserFilter selectedFilter) {
		while (filename.endsWith(".")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		filename = filename.trim();
		if (filename.length() == 0) {
			return null;
		}
		return filename;
	}

	protected final JDialog createDialog(Component parent) {
		dialog = super.createDialog(parent);
		return dialog;
	}

	protected boolean endsWithSelectedExtension(String name) {
		BrowserFilter filter = (BrowserFilter) getFileFilter();

		final int len = name.length();
		Iterator it = filter.extensions.iterator();
		String ext;
		while (it.hasNext()) {
			ext = it.next().toString();
			int extLen = ext.length();
			if (name.substring(len - extLen).equalsIgnoreCase(ext)) {
				return true;

			}
		}
		return false;
	}

	protected JDialog getDialog() {
		return dialog;
	}

	protected boolean hasValidExtension(File file, BrowserFilter filter) {
		String filename = file.toString().trim();
		final int index = filename.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = filename.substring(index + 1);
		if (extension.endsWith(".")) {
			return false;
		}
		return true;
	}

	private final void hideViewer() {
		if (previewing) {
			previewing = false;
			((FileBrowserUI) getUI()).hideViewer();
		}
	}

	public final File open(
		StandardMainFrame parent,
		String title,
		BrowserFilter[] filters,
		BrowserFilter selectedFilter,
		boolean showIconViewer) {

		prepareDialog(
			parent,
			title,
			filters,
			selectedFilter,
			showIconViewer,
			openPath);
		int result = showOpenDialog(parent);
		if (result == APPROVE_OPTION) {
			File f = getSelectedFile();
			openPath = f.getParent();
			return f;
		}
		return null;
	}

	private final void prepareDialog(
		StandardMainFrame parent,
		String title,
		BrowserFilter[] filters,
		BrowserFilter selectedFilter,
		boolean showIconViewer,
		String path) {
		if (showIconViewer) {
			showViewer();
		}
		else {
			hideViewer();
		}
		resetChoosableFileFilters();
		if ((selectedFilter == null)
			&& ((filters == null) || (filters.length == 0))) { // kein filter
			addChoosableFileFilter(ALL_FILES_FILTER);
		}
		else {
			if (filters != null) {
				final int len = filters.length;
				for (int i = 0; i < len; i++) {
					addChoosableFileFilter(filters[i]);
				}
			}
			if (selectedFilter != null) {
				setFileFilter(selectedFilter);
			}
		}
		setDialogTitle(title);
		setCurrentDirectory(new File(path));
		if (showIconViewer) {
			resetViewer();
		}
	}

	public final File save(
		StandardMainFrame parent,
		String title,
		BrowserFilter[] filters,
		BrowserFilter selectedFilter,
		boolean showIconViewer) {
		prepareDialog(
			parent,
			title,
			filters,
			selectedFilter,
			showIconViewer,
			savePath);
		int result = showSaveDialog(parent);
		if (result == APPROVE_OPTION) {
			File f = getSelectedFile();
			savePath = f.getParent();
			BrowserFilter selFilter = (BrowserFilter) getFileFilter();
			if (!hasValidExtension(f, selFilter)) {
				f = new File(adjustFilename(f.toString(), selFilter));
			}
			return f;
		}
		return null;
	}

	private void resetViewer() {
		((FileBrowserUI) getUI()).getViewer().rebuild();
	}

	private final void showViewer() {
		if (!previewing) {
			previewing = true;
			((FileBrowserUI) getUI()).showViewer();
		}
	}

	private final void setIgnoreKeys(boolean b) {
		ignoreKeys = b;
	}

	public void windowGainedFocus(WindowEvent e) {
		setIgnoreKeys(false);
	}

	public void windowLostFocus(WindowEvent e) {
		setIgnoreKeys(true);
	}

	// selected file property changed
	public void propertyChange(PropertyChangeEvent evt) {
		((FileBrowserUI) getUI()).getViewer().selectionChanged(evt);
	}

}
