package jdraw.maglie;

import java.awt.event.ActionEvent;

import jdraw.action.DrawAction;
import jdraw.gui.MagliaTool;
import jdraw.gui.ToolPanel;
import magliera.puntoMaglia.TipoLavoroEnum;

public class MagliaUnitaAction extends DrawAction {
	
	public MagliaUnitaAction() {
		super("Lavorazione Maglia Unita", "maglie/maglia_unita.png");
		setToolTipText("Lavorazione Maglia Unita");
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Selezionato tipo lavoro: -> "+TipoLavoroEnum.values()[4].toString());
		ToolPanel.INSTANCE.setCurrentTool(new MagliaTool(TipoLavoroEnum.values()[4].toString()));
		
	}

}
