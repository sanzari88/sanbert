package util.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import util.Log;

/*
 * StandardWindow.java - created on 17.11.2003
 * 
 * @author Michaela Behling
 */

public class StandardWindow extends JFrame implements KeyEventDispatcher {

	public static final int CANCEL = 0;
	public static final int APPROVE = 1;

	private JComponent firstFocusComponent = null;
	private JButton approveButton = null;
	private JButton cancelButton = null;
	private int result = CANCEL;
	private boolean disposeOnClose = true;
	public final JPanel main = new JPanel(new BorderLayout(0, 0));
	private final JPanel buttons = new JPanel(new BorderLayout(0, 0));

	private final JPanel leftButtons =
		new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	private final JPanel rightButtons =
		new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

	private final boolean ignoreEscapeKey;
	private final ArrayList buttonList = new ArrayList();
	private final StandardMainFrame mainFrame;

	public StandardWindow(
		StandardMainFrame parent,
		String title,
		boolean ignoreESCKey) {
		super(title);
		mainFrame = parent;
		ignoreEscapeKey = ignoreESCKey;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setContentPane(main);

		buttons.add(leftButtons, BorderLayout.WEST);
		buttons.add(rightButtons, BorderLayout.EAST);
		buttons.setBorder(new EmptyBorder(15, 0, 0, 0));

		addWindowListener(new WindowAdapter() {
			public final void windowOpened(WindowEvent e) {
				if (firstFocusComponent != null) {
					firstFocusComponent.grabFocus();
				}
				FocusManager
					.getCurrentKeyboardFocusManager()
					.addKeyEventDispatcher(
					StandardWindow.this);
			}

			public final void windowClosed(WindowEvent e) {
				FocusManager
					.getCurrentKeyboardFocusManager()
					.removeKeyEventDispatcher(
					StandardWindow.this);
			}

		});
	}

	public final boolean isIconfied() {
		return (getExtendedState() & ICONIFIED) != 0;
	}

	public StandardWindow(StandardMainFrame parent, String title) {
		this(parent, title, false);
	}

	public void setDisposeOnClose(boolean flag) {
		disposeOnClose = flag;
	}

	public final int getResult() {
		return result;
	}

	protected final void setFirstFocusComponent(JComponent c) {
		firstFocusComponent = c;
	}

	public final void setDefaultBorder() {
		main.setBorder(
			new CompoundBorder(
				new EtchedBorder(),
				new EmptyBorder(10, 10, 10, 10)));
	}

	public final void addLeftButton(JButton button) {
		buttonList.add(button);
		leftButtons.add(button);
		leftButtons.add(Box.createHorizontalStrut(4));
	}

	public final void addRightButton(JButton button) {
		buttonList.add(button);
		rightButtons.add(Box.createHorizontalStrut(4));
		rightButtons.add(button);
	}

	public final void addButtonPanel() {
		main.add(buttons, BorderLayout.SOUTH);
	}

	protected void approve() {
		result = APPROVE;
		close();
	}

	protected void cancel() {
		result = CANCEL;
		close();
	}

	private void resizeButtons() {
		int maxHeight = 0;
		int h;
		Iterator it = buttonList.iterator();
		while (it.hasNext()) {
			h = ((JButton) it.next()).getPreferredSize().height;
			if (h > maxHeight) {
				maxHeight = h;
			}
		}
		it = buttonList.iterator();
		JButton b;
		Dimension dim;
		while (it.hasNext()) {
			b = (JButton) it.next();
			dim = b.getPreferredSize();
			dim.height = maxHeight;
			b.setPreferredSize(dim);
		}
	}

	public JButton getApproveButton() {
		if (approveButton == null) {
			approveButton = new JButton("OK");
			approveButton.setMnemonic('o');
			approveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					approve();
				}
			});
		}
		return approveButton;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setMnemonic('c');
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
		}
		return cancelButton;
	}

	public final void open() {
		if (buttonList.size() > 0) {
			resizeButtons();
			buttons.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
		}
		pack();
		center();
		setVisible(true);
	}

	public final void close() {
		setVisible(false);
		if (disposeOnClose) {
			dispose();
		}
	}

	public final void center() {
		Dimension screen;
		if ((mainFrame == null)
			|| !mainFrame.isVisible()
			|| mainFrame.isIconfied()) {
			screen = Toolkit.getDefaultToolkit().getScreenSize();
		}
		else {
			screen = mainFrame.getSize();
		}
		Dimension dim = getPreferredSize();
		move(
			screen.width / 2 - dim.width / 2,
			screen.height / 2 - dim.height / 2);
	}

	protected boolean handleKey(KeyEvent e) {
		return false;
	}

	public final boolean dispatchKeyEvent(KeyEvent e) {
		if (!isActive()) {
			return false;
		}
		if (e.getID() == KeyEvent.KEY_TYPED) {
			if ((e.getKeyChar() == 0x1b) && (e.getModifiers() == 0)) { // ESC
				if (!ignoreEscapeKey) {
					cancel();
					return true;
				}
			}
		}
		return handleKey(e);
	}
}