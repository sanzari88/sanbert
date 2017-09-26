package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;

public class Compilatore {
	
	private Maglia[][] matriceMaglia;
	private HashMap <Integer,String> tabellaColori;
	private ArrayList<LavoroCaduta> righeLavoro;
	private ArrayList<TrasportoCaduta> righeTrasporto;
	
	public Compilatore(Maglia[][] matriceMaglia) {
		this.matriceMaglia= matriceMaglia;
		tabellaColori = new HashMap<>();
		righeLavoro = new ArrayList<>();
		righeTrasporto = new ArrayList<>();
		tabellaColori.put(16, "a");
		tabellaColori.put(24, "b");
		// capovolgendo la matrice ho il disegno in ordine di istruzioni partendo dal basso, quindi da 1
		Maglia [][] matriceMaglieTmp = Utility.capovolgiMatrice(matriceMaglia);
		elabora(matriceMaglieTmp);
	}
	
	public String getProgrammaElaborato() {
		return "";
	}
	
	private void elabora(Maglia[][] matriceMaglia) {
		creaFileStruttura();
		creaComandiLavoro(matriceMaglia);
		creaComandiTrasporti(matriceMaglia);
		GestoreCadute gestore = new GestoreCadute();
		gestore.elaborazioneFinale(righeLavoro,righeTrasporto);
	}
	
	private void creaComandiTrasporti(Maglia[][] matriceMaglia) {
		int nr = matriceMaglia.length-1;
		int nc = matriceMaglia[0].length;
		TrasportoCaduta trasporto ;
		
		String traspAD = "";
		String traspDA = "";
		
		for(int r= 0 ;r< nr; r++) {
			for(int c =0; c < nc; c++) { 
				Maglia attuale = matriceMaglia[r][c];
				Maglia rigaSccessiva = matriceMaglia[r+1][c];
				// Trasporta da avanti a dietro
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
					traspAD=traspAD+getLetteraColoreFromMaglia(attuale.getColore());
				}
				
				// trasporto dietro avanti
				
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
					traspDA.concat(getLetteraColoreFromMaglia(attuale.getColore()));
				}
			}
			trasporto = new TrasportoCaduta();
			trasporto.setPosizione(1);
			trasporto.setDietroAvanti(traspDA);
			trasporto.setAvantiDietro(traspAD);
			trasporto.setRigaDisegno(nr);
			
			righeTrasporto.add(trasporto);
		}
	}

	private boolean creaFileStruttura() {
		File f = new File("Struttura.txt");
		try {
			PrintStream ps = new PrintStream(f);
			ps.println("#-------- Programma di generazione automatico by Raffaele Sanzari --------#");
			
			
			int nr = matriceMaglia.length-1;
			int nc = matriceMaglia[0].length;
			int indiceDisegno= nr;
			
			
			for(int r=0 ;r<= nr; r++) {
				String indiceNormalizzato= Utility.normalizzaIndiceDisegno(indiceDisegno+1);
				System.out.print(indiceNormalizzato+" = '");
				ps.print(indiceNormalizzato+" = '");
				for(int i=0; i<nc; i++) { 
					 Maglia m = matriceMaglia[r][i];
					 String colore = getLetteraColoreFromMaglia(m.getColore());
					 ps.print(colore);
					 System.out.print(colore);;
				}
				ps.println("';");
				System.out.println("';");
				indiceDisegno--;
			}
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	private boolean creaComandiLavoro(Maglia[][] matriceMaglia) {
			
			int nr = matriceMaglia.length-1;
			
			for(int i=0;i< nr; i++) {
			
			LavoroCaduta lavoro =getMaglieLavoro(matriceMaglia[i], i);
			righeLavoro.add(lavoro);
			}
			
		
		
		return true;
	}

	private String getLetteraColoreFromMaglia(int colore) {
		// TODO Auto-generated method stub
		return tabellaColori.get(colore);
	}
	
	private LavoroCaduta getMaglieLavoro(Maglia[] rigaMatriceMaglia, int rigaDisegno) {
		String colore ="";
		String ant = "";
		String post ="";
		String inglA ="";
		String inglP = "";
		LavoroCaduta lavoro = new LavoroCaduta();
		
		for(Maglia m: rigaMatriceMaglia) {
			colore = getLetteraColoreFromMaglia(m.getColore());
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
				if(!ant.contains(colore))
					ant=ant+colore;
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
				if(!post.contains(colore))
					post=post+colore;
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaIngleseAnteriore.toString())) {
				if(!inglA.contains(colore))
					inglA=inglA+colore;
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaInglesePosteriore.toString())) {
				if(!inglP.contains(colore))
					inglP=inglP+colore;
			}
				
		}
		lavoro.setAnteriore(ant);
		lavoro.setPosteriore(post);
		lavoro.setIngleseAnt(inglA);
		lavoro.setInglesePost(post);
		lavoro.setRigaDisegno(rigaDisegno);
		
		return lavoro;
	}

}
