package jdraw.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import elaborazioneMaglia.Compilatore;
import jdraw.data.Clip;

public class CompilaAction extends DrawAction{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected CompilaAction() {
		super("Compila sorgente","view_anim.png");
		setToolTipText("Compila sorgetne");
		setAccelerators(
			new KeyStroke[] { KeyStroke.getKeyStroke(new Character('M'), 0)});
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Avvio compilazione");
		Compilatore c = new Compilatore(Clip.getMatriceMaglia(),Clip.getMatriceComandi(),Clip.getBalza());
		
	}

}
