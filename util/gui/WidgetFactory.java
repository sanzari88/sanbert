package util.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/*
 * WidgetFactory.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public final class WidgetFactory {

	public static final FocusAdapter TEXTFIELD_FOCUS_ADAPTER =
		new FocusAdapter() {
		public void focusGained(FocusEvent e) {
			((JTextField) e.getComponent()).selectAll();
		}
	};

	private WidgetFactory() {
	}

	public static void addFocusAdapter(JTextComponent component) {
		component.addFocusListener(TEXTFIELD_FOCUS_ADAPTER);
	}
}
