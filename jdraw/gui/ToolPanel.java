package jdraw.gui;

import jdraw.action.*;
import jdraw.maglie.MagliaDirittoAction;
import jdraw.maglie.MagliaInglAntAction;
import jdraw.maglie.MagliaInglPostAction;
import jdraw.maglie.MagliaRovescioAction;
import sun.util.resources.cldr.tzm.CurrencyNames_tzm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import util.Log;
import util.ResourceLoader;
import util.gui.AntialiasPanel;

/*
 * Created on 28-Oct-2003
 *
 * @author michaela
 */

public final class ToolPanel extends JPanel {

	private static final Insets INSETS = new Insets(1,1,0,0);

	public static final int ICON_SIZE = 28;
	public static final ToolPanel INSTANCE = new ToolPanel();
	private static final Dimension BUTTON_DIMENSION = new Dimension(38, 38);
	private static final int SKIP = 8;

	private Tool currentTool;
	private String tipoLavoroSelezionato;
	public final ButtonGroup toolGroup = new ButtonGroup();
	private ToolButton pixelButton;
	private ToolButton antialiasButton;
	private ToolButton gradientFillButton;

	private ToolPanel() {
		super(new BorderLayout(6, 0));
		setBorder(new EmptyBorder(0, 0, 4, 0));
		currentTool = PixelTool.INSTANCE;
		createGui();
	}

	public Tool getCurrentTool() {
		return currentTool;
	}
	
	
	public void setMagliaSelezionata(String tipoLavoro) {
		tipoLavoroSelezionato= tipoLavoro;
		
	}
	
	public String getMagliaSelezionata() {
		return tipoLavoroSelezionato;
	}
	public void setCurrentTool(Tool aTool) {
		if (currentTool != aTool && currentTool != null) {
			if(!aTool.isStrumentoMaglia()) {
			if (currentTool != null) {
				currentTool.deactivate();
			}
			currentTool = aTool;
			if (currentTool == PixelTool.INSTANCE) {
				pixelButton.setSelected(true);
			}
			currentTool.activate();
			DrawAction.getAction(ToggleAntialiasAction.class).setEnabled(
				currentTool.supportsAntialias());
			DrawAction.getAction(ToggleGradientFillAction.class).setEnabled(
				currentTool.supportsGradientFill());

		}
			else
			{
				// è uno strumento maglia. Seleziono solo il tipo di lavoro
				String tipoLavoro= aTool.getMagliaSelezionata();
				setMagliaSelezionata(tipoLavoro);
			}
	}
	}

	private void createGui() {
		JPanel clickBar = createClickBar();
		add(clickBar, BorderLayout.WEST);

		JScrollPane pane = new JScrollPane(Tool.getPreview());
		Border border = pane.getBorder();
		pane.setBorder(new CompoundBorder(new TitledBorder("Anteprima"), border));
		pane.setPreferredSize(new Dimension(10, 10));
		add(pane, BorderLayout.CENTER);
	}

	private JPanel createClickBar() {
		JPanel panel = new AntialiasPanel(new GridBagLayout());
		JPanel tools = createTools();
		JPanel actions = createActions();
		JPanel tipoMaglia =createMagliaTools();

		// panel anordnen				
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;

		panel.add(actions, gc);

		gc.gridx++;
		panel.add(tools, gc);
		
		gc.gridx++;
		panel.add(tipoMaglia,gc);

		return panel;
	}

	private JPanel createActions() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Tasti rapidi"));

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = INSETS;
		gc.gridx = 0;
		gc.gridy = 0;

		ActionButton button;
		button =
			new ActionButton(DrawAction.getAction(SaveAction.class), "save.png");
		p.add(button, gc);

		gc.gridx++;
		button =
			new ActionButton(DrawAction.getAction(LoadAction.class), "open.png");
		p.add(button, gc);

		gc.gridx++;
		button =
			new ActionButton(DrawAction.getAction(UndoAction.class), "undo.png");
		p.add(button, gc);

		gc.gridx++;
		button =
			new ActionButton(DrawAction.getAction(RedoAction.class), "redo.png");
		p.add(button, gc);

		gc.gridx = 0;
		gc.gridy++;
		button =
			new ActionButton(
				DrawAction.getAction(AddFrameAction.class),
				"frame_new.png");
		p.add(button, gc);

		gc.gridx++;
		button =
			new ActionButton(DrawAction.getAction(RemoveFrameAction.class),"delete_frame.png");
		p.add(button, gc);

		gc.gridx++;			// Bottone che avvia la compilazione
		button =
			new ActionButton(DrawAction.getAction(CompilaAction.class),"view_anim.png");
		p.add(button, gc);

		gc.gridx++;
		//		button =
		//			new ActionButton(DrawAction.getAction(HelpAction.class), "help.png");
		button =
			new ActionButton(DrawAction.getAction(CropAction.class), "crop.png");
		p.add(button, gc);

		return p;
	}

	public void selectAntialias(boolean flag) {
		antialiasButton.setSelected(flag);
	}

	public void selectGradientFill(boolean flag) {
		gradientFillButton.setSelected(flag);
	}
	
	private JPanel createMagliaTools() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Maglie elementari"));
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = INSETS;
		gc.gridx = 0;
		gc.gridy = 0;
		
		TipoMagliaButton button;
		button =
				new TipoMagliaButton(
					DrawAction.getAction(MagliaDirittoAction.class),"maglia_diritto.png");
		p.add(button, gc);
		gc.gridx++;
		
		button =
			new TipoMagliaButton(
				DrawAction.getAction(MagliaRovescioAction.class),"maglia_rovescio.png");
		//toolGroup.add(button);
		p.add(button, gc);
		gc.gridy++;
		gc.gridx = 0;
		
		button =
				new TipoMagliaButton(
					DrawAction.getAction(MagliaInglAntAction.class),"ingleseAnt.png");
			//toolGroup.add(button);
			p.add(button, gc);
		gc.gridx++;	
			
			button =
					new TipoMagliaButton(
						DrawAction.getAction(MagliaInglPostAction.class),"inglesePost.png");
				//toolGroup.add(button);
				p.add(button, gc);
		
		return p;
	}

	private JPanel createTools() {

		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new TitledBorder("Strumenti disegno"));
		ToolButton button;
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = INSETS;
		gc.gridx = 0;
		gc.gridy = 0;

		button =
			new ToolButton(
				DrawAction.getAction(SetPixelToolAction.class),
				"pixel_tool.png");
		toolGroup.add(button);
		p.add(button, gc);
		toolGroup.setSelected(button.getModel(), true);
		pixelButton = button;

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetFillToolAction.class),
				"fill_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetColourPickerToolAction.class),
				"colorpicker.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetLineToolAction.class),
				"line_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		p.add(Box.createHorizontalStrut(SKIP), gc);

		gc.gridx++;
//		button =
//			new ToolButton(
//				DrawAction.getAction(ToggleGradientFillAction.class),
//				"gradient_fill.png");
//		p.add(button, gc);
//		gradientFillButton = button;

//		gc.gridx++;
//		button =
//			new ToolButton(
//				DrawAction.getAction(ToggleAntialiasAction.class),
//				"antialias_on.png");
//		p.add(button, gc);
//		antialiasButton = button;
//		button.setSelected(true);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				JToggleButton button = (JToggleButton) e.getSource();
//				if (button.isSelected()) {
//					button.setIcon(
//						ResourceLoader.getImage(
//							"jdraw/images/antialias_on.png",
//							ICON_SIZE));
//				}
//				else {
//					button.setIcon(
//						ResourceLoader.getImage(
//							"jdraw/images/antialias_off.png",
//							ICON_SIZE));
//				}
//			}
//		});

		gc.gridx = 0;
		gc.gridy++;

		button =
			new ToolButton(
				DrawAction.getAction(SetRectangleToolAction.class),
				"rectangle_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetFilledRectangleToolAction.class),
				"filled_rectangle_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetOvalToolAction.class),
				"oval_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetFilledOvalToolAction.class),
				"filled_oval_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		gc.gridx++;
		p.add(Box.createHorizontalStrut(SKIP), gc);

		gc.gridx++;
//		button =
//			new ToolButton(
//				DrawAction.getAction(SetClipToolAction.class),
//				"clip_tool.png");
//		toolGroup.add(button);
//		p.add(button, gc);

		gc.gridx++;
		button =
			new ToolButton(
				DrawAction.getAction(SetTextToolAction.class),
				"text_tool.png");
		toolGroup.add(button);
		p.add(button, gc);

		return p;
	}
	
	// Tipo Maglia Button
	
	public final class TipoMagliaButton extends JToggleButton{
		
		public TipoMagliaButton(DrawAction action, String iconName) {
			super(action);
			setFocusPainted(true);
			this.setToolTipText(action.getToolTipText());
			setText(null);
			setIcon(
				ResourceLoader.getImage("jdraw/images/maglie/" + iconName, ICON_SIZE));
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
		}

		//	warum ist das n�tig? muss ein bug sein.
		protected void processComponentKeyEvent(KeyEvent e) {
			e.consume();
		}
		public Dimension getPreferredSize() {
			return BUTTON_DIMENSION;
		}
		
	}

	// Tool Button

	public final class ToolButton extends JToggleButton {

		public ToolButton(DrawAction action, String iconName) {
			super(action);
			setFocusPainted(false);
			this.setToolTipText(action.getToolTipText());
			setText(null);
			setIcon(
				ResourceLoader.getImage("jdraw/images/" + iconName, ICON_SIZE));
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
		}

		//	warum ist das n�tig? muss ein bug sein.
		protected void processComponentKeyEvent(KeyEvent e) {
			e.consume();
		}
		public Dimension getPreferredSize() {
			return BUTTON_DIMENSION;
		}
	}

	//	Action Button

	public final class ActionButton extends JButton {

		public ActionButton(DrawAction action, String iconName) {
			super(action);
			setFocusPainted(false);
			this.setToolTipText(action.getToolTipText());
			setText(null);
			setIcon(
				ResourceLoader.getImage("jdraw/images/" + iconName, ICON_SIZE));
		}

		// warum ist das n�tig? muss ein bug sein.
		protected void processComponentKeyEvent(KeyEvent e) {
			e.consume();
		}

		public Dimension getPreferredSize() {
			return BUTTON_DIMENSION;
		}
	}

}
