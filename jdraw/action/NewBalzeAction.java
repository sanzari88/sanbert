package jdraw.action;

import java.awt.event.ActionEvent;

import jdraw.gui.DrawDialog;
import jdraw.gui.NewBalzaDialog;


	public class NewBalzeAction extends DrawAction {

		protected NewBalzeAction() {
			super("Balza");
			setToolTipText("Seleziona tipo balza");
		}

		public void actionPerformed(ActionEvent e) {
			
			NewBalzaDialog d= new NewBalzaDialog();
			d.open();
			String costinaSelezionata;
			if (d.getResult() == DrawDialog.APPROVE) {
				costinaSelezionata=d.getCostina();
			}
		}

	}

