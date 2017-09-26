package jdraw.maglie;

import java.awt.event.ActionEvent;

import jdraw.action.DrawAction;
import jdraw.gui.MagliaTool;
import jdraw.gui.ToolPanel;
import magliera.puntoMaglia.TipoLavoroEnum;

public class MagliaRovescioAction extends DrawAction{

	public MagliaRovescioAction() {
		super("Lavorazione Maglia Rovescio", "maglie/maglia_rovescio.png");
		setToolTipText("Lavorazione Maglia Rovescio");
	}

	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Selezionato tipo lavoro: -> "+TipoLavoroEnum.values()[1].toString());
		ToolPanel.INSTANCE.setCurrentTool(new MagliaTool(TipoLavoroEnum.values()[1].toString()));
		
	}

}
