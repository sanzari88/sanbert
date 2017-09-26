package jdraw.maglie;

import java.awt.event.ActionEvent;

import jdraw.action.DrawAction;
import jdraw.gui.MagliaTool;
import jdraw.gui.ToolPanel;
import magliera.puntoMaglia.TipoLavoroEnum;

public class MagliaInglPostAction extends DrawAction{
	
	public MagliaInglPostAction() {
		super("Lavorazione Maglia Inglese Posteriore", "maglie/inglesePost.png");
		setToolTipText("Lavorazione Maglia Inglese Posteriore");
	}

	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Selezionato tipo lavoro: -> "+TipoLavoroEnum.values()[3].toString());
		ToolPanel.INSTANCE.setCurrentTool(new MagliaTool(TipoLavoroEnum.values()[3].toString()));
		
	}

}
