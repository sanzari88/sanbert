package jdraw.maglie;

import java.awt.event.ActionEvent;

import jdraw.action.DrawAction;
import jdraw.gui.MagliaTool;
import jdraw.gui.ToolPanel;
import magliera.puntoMaglia.TipoLavoroEnum;

public class MagliaInglAntAction extends DrawAction{
	
	public MagliaInglAntAction() {
		super("Lavorazione Maglia Inglese Anteriore", "maglie/ingleseAnt.png");
		setToolTipText("Lavorazione Maglia Inglese Anteriore");
	}

	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Selezionato tipo lavoro: -> "+TipoLavoroEnum.values()[2].toString());
		ToolPanel.INSTANCE.setCurrentTool(new MagliaTool(TipoLavoroEnum.values()[2].toString()));
		
	}

}
