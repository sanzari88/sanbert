package util.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

import util.Log;

public class FileBrowserUI
	extends MetalFileChooserUI
	implements ChangeListener {

	private final FileBrowser chooser;

	public static final String FILE_BROWSER_UI = "util.gui.FileBrowserUI";
	private static boolean usePreview = System.getProperty("nopreview") == null;

	public static ComponentUI createUI(JComponent c) {
		return new FileBrowserUI((FileBrowser) c);
	}

	private JCheckBox previewCheck;
	private IconViewer viewer;

	public FileBrowserUI(JFileChooser aChooser) {
		super(aChooser);
		if (aChooser instanceof FileBrowser) {
			chooser = (FileBrowser) aChooser;
		}
		else {
			chooser = null;
		}
	}

	public IconViewer getViewer() {
		return viewer;
	}

	private boolean isFileBrowser() {
		return chooser != null;
	}

	public void hideViewer() {
		if (isFileBrowser() && usePreview) {
			getModel().removeListDataListener(viewer);
			getBottomPanel().remove(viewer);
			updateDialog();
		}
	}

	public void installComponents(final JFileChooser fc) {
		fc.setApproveButtonText("OK");
		fc.setApproveButtonToolTipText("OK");

		super.installComponents(fc);

		if (isFileBrowser()) {
			viewer = new IconViewer(getModel());
			previewCheck = new JCheckBox("Show Preview");

			JPanel bottom = getBottomPanel();
			JPanel buttons = getButtonPanel();			
			JPanel p = new JPanel(new BorderLayout(0, 0));
			bottom.remove(buttons);						
			bottom.add(p);
			
			p.add(previewCheck, BorderLayout.WEST);
			previewCheck.setVerticalAlignment(SwingConstants.BOTTOM);	
			previewCheck.setBorder(new EmptyBorder(0,0,4,0));		
			p.add(buttons,BorderLayout.EAST); 
			
			previewCheck.setSelected(usePreview);
			previewCheck.addChangeListener(this);

			if (usePreview) {
				bottom.add(viewer);
			}			
		}
	}

	public void showViewer() {
		if (isFileBrowser() && usePreview) {
			getModel().addListDataListener(viewer);
			getBottomPanel().add(viewer);
			viewer.updateSelection();
			updateDialog();
		}
	}

	private void updateDialog() {
		if (chooser.getDialog() != null)
			chooser.getDialog().pack();
	}

	public void stateChanged(ChangeEvent e) {
		if (previewCheck.isSelected()) {
			showViewer();
		}
		else {
			hideViewer();
		}
	}

}
