package jdraw.maglie;

import java.awt.event.ActionEvent;

import jdraw.action.DrawAction;
import jdraw.gui.LineTool;
import jdraw.gui.MagliaTool;
import jdraw.gui.PixelTool;
import jdraw.gui.ToolPanel;
import magliera.puntoMaglia.TipoLavoroEnum;

public class MagliaDirittoAction extends DrawAction{

	public MagliaDirittoAction() {
		super("Lavorazione Maglia Diritto", "maglie/maglia_diritto.png");
		setToolTipText("Lavorazione Maglia Diritto");
	}

	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Selezionato tipo lavoro: -> "+TipoLavoroEnum.values()[0].toString());
		ToolPanel.INSTANCE.setCurrentTool(new MagliaTool(TipoLavoroEnum.values()[0].toString()));
		
	}

}
