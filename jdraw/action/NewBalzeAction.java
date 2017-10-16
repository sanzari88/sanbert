package jdraw.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import Inizio.Balza;
import jdraw.data.Clip;
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
				Balza b= d.getCostina();
				Clip.setBalza(b);
				costinaSelezionata=b.toString();
				System.out.println(costinaSelezionata);
				
			}
		}
		
		

	}

