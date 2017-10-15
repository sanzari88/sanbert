package util.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;



public final class WidgetFactory {

	public static final FocusAdapter TEXTFIELD_FOCUS_ADAPTER =
		new FocusAdapter() {
		public void focusGained(FocusEvent e) {
			((JTextField) e.getComponent()).selectAll();
		}
	};
	
	public static final FocusAdapter JComboBox_FOCUS_ADAPTER =
			new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				 ((JComboBox) e.getComponent()).getSelectedItem();
			}
		};

	private WidgetFactory() {
	}

	public static void addFocusAdapterJComboBox(JComboBox costina) {
		costina.addFocusListener(JComboBox_FOCUS_ADAPTER);
	}
	
	public static void addFocusAdapter(JTextComponent component) {
		component.addFocusListener(TEXTFIELD_FOCUS_ADAPTER);
	}
}
