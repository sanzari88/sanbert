package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import jdraw.data.Palette;
import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;

public class Compilatore {
	
	private Maglia[][] matriceMaglia;
	private HashMap <Integer,String> tabellaColori;
	private ArrayList<LavoroCaduta> righeLavoro;
	private ArrayList<TrasportoCaduta> righeTrasporto;
	
	public Compilatore(Maglia[][] matriceMaglia) {
		this.matriceMaglia= matriceMaglia;
		tabellaColori = Palette.getTabellaColoriMaglia();
//		tabellaColori.put(16, "a");
//		tabellaColori.put(24, "b");
		// capovolgendo la matrice ho il disegno in ordine di istruzioni partendo dal basso, quindi da 1
		Maglia [][] matriceMaglieTmp = Utility.capovolgiMatrice(matriceMaglia);
		elabora(matriceMaglieTmp);
	}
	
	public String getProgrammaElaborato() {
		return "";
	}
	
	private void elabora(Maglia[][] matriceMaglia) {
		creaFileStruttura();
		System.out.println("File struttura creato");
		
		righeLavoro = creaComandiLavoro(matriceMaglia);
		System.out.println("Comandi lavoro creati");
		
		righeTrasporto = creaComandiTrasporti(matriceMaglia);
		System.out.println("Comandi trasporti creati");
		
		GestoreCadute gestore = new GestoreCadute();
		ArrayList<Caduta> lavoro =gestore.elaborazioneFinale(righeLavoro,righeTrasporto);
		System.out.println("Corse gestite correttamente");
		
		String comandiMacchina= gestore.trasformaComandiMacchina(lavoro);
		creaFileComandi(comandiMacchina);
		System.out.println("File comandi generato correttamente");
	}
	
	private void creaFileComandi(String comandiMacchina) {
		File f = new File("Comandi.txt");
		try {
			PrintStream ps = new PrintStream(f);
			ps.print(comandiMacchina);
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<TrasportoCaduta> creaComandiTrasporti(Maglia[][] matriceMaglia) {
		ArrayList<TrasportoCaduta> righeTrasporto = new ArrayList<>();
		int nr = matriceMaglia.length-1;
		int nc = matriceMaglia[0].length;
		int righaDisegno=0;
		TrasportoCaduta trasporto ;
		
		
		
		for(int r= 0 ;r< nr; r++) {	
			String traspAD = "";
			String traspDA = "";
			
			for(int c =0; c < nc; c++) { 
				Maglia attuale = matriceMaglia[r][c];
				Maglia rigaSccessiva = matriceMaglia[r+1][c];
				// Trasporta da avanti a dietro
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
					if(!traspAD.contains(getLetteraColoreFromMaglia(attuale.getColore()))) {
					traspAD=traspAD+getLetteraColoreFromMaglia(attuale.getColore());
					}
				}
				
				// trasporto dietro avanti
				
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
					if(!traspDA.contains(getLetteraColoreFromMaglia(attuale.getColore()))) {
						traspDA=traspDA+getLetteraColoreFromMaglia(attuale.getColore());
					}
				}
			}
			if(traspAD.length()>0 || traspDA.length()>0) {
			trasporto = new TrasportoCaduta();
			trasporto.setPosizione(1);
			trasporto.setDietroAvanti(traspDA);
			trasporto.setAvantiDietro(traspAD);
			trasporto.setRigaDisegno(righaDisegno);
			
			righeTrasporto.add(trasporto);
		}
			righaDisegno++;
		}
		
		return righeTrasporto;
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
	
	private ArrayList<LavoroCaduta> creaComandiLavoro(Maglia[][] matriceMaglia) {
		ArrayList<LavoroCaduta> righeLavoro = new ArrayList<>();
			int nr = matriceMaglia.length;
			
			for(int i=0;i< nr; i++) {
				LavoroCaduta lavoro =getMaglieLavoro(matriceMaglia[i], i);
				righeLavoro.add(lavoro);
			}
			
		
		
		return righeLavoro;
	}

	private String getLetteraColoreFromMaglia(int colore) {
		// TODO Auto-generated method stub
		return tabellaColori.get(colore);
	}
	
	private LavoroCaduta getMaglieLavoro(Maglia[] rigaMatriceMaglia, int rigaDisegno) {
		String colore ="";
		ArrayList<String> colors = new ArrayList<>();
		String ant = "";
		String post ="";
		String inglA ="";
		String inglP = "";
		String unita = "";
		ArrayList<Colore> colori = new ArrayList<>();
		LavoroCaduta lavoro = new LavoroCaduta();
		
		for(Maglia m: rigaMatriceMaglia) {
			if(!colore.contains(getLetteraColoreFromMaglia(m.getColore()))) {
				colore= colore + getLetteraColoreFromMaglia(m.getColore());
				colors.add(getLetteraColoreFromMaglia(m.getColore()));
			}
		}
			
			for(String col : colors) {
				// scorro la riga per ogni colore
				Colore c = new Colore();
				c.setColore(col);
				for(Maglia m1: rigaMatriceMaglia) {
					if(getLetteraColoreFromMaglia(m1.getColore()).equalsIgnoreCase(col)) {
						
						if(m1.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
							c.addAnteriore(m1);
						}
						
						if(m1.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
							c.addPosteriore(m1);
						}
						
						if(m1.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaIngleseAnteriore.toString())) {
							c.addInglAnt(m1);
						}
						
						if(m1.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaInglesePosteriore.toString())) {
							c.addInglPost(m1);
						}
						
						if(m1.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaUnita.toString())) {
							c.addUnita(m1);
						}
					}
				}
				
				colori.add(c);
			}
			
			for(Colore col: colori) {
				// per ogni colore verifico se devo riealborare le maglie
				if(!col.isMaglieUgualiColore()) {
					riealoboraColoriAndMaglie(colors,col.getAnteriore());
					riealoboraColoriAndMaglie(colors,col.getPosteriore());
					riealoboraColoriAndMaglie(colors,col.getInglAnt());
					riealoboraColoriAndMaglie(colors,col.getInglPost());
					riealoboraColoriAndMaglie(colors,col.getUnita());
				}
			}
			
			ricomponiRigaMatriceMaglia(colori, rigaMatriceMaglia);
			
			
			for(Maglia m: rigaMatriceMaglia) {
				String color=Character.toString((char)m.getColore());
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
				if(m.getNewColor()==0) {
					if(!ant.contains(color))
						ant=ant+color;
				}
				else {
					if(!ant.contains(Character.toString((char)m.getNewColor())))
						ant=ant+Character.toString((char)m.getNewColor());
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
				if(m.getNewColor()==0) {
					if(!post.contains(color))
						post=post+color;
				}
				else {
					if(!post.contains(Character.toString((char)m.getNewColor())))
						post=post+Character.toString((char)m.getNewColor());
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaIngleseAnteriore.toString())) {
				if(m.getNewColor()==0) {
					if(!inglA.contains(Character.toString((char)m.getNewColor())))
						inglA=inglA+Character.toString((char)m.getNewColor());
				}
				else {
					if(!inglA.contains(Character.toString((char)m.getNewColor())))
						inglA=inglA+Character.toString((char)m.getNewColor());
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaInglesePosteriore.toString())) {
				if(m.getNewColor()==0) {
					if(!inglP.contains(Character.toString((char)m.getNewColor())))
						inglP=inglP+Character.toString((char)m.getNewColor());
				}
				else {
					if(!inglP.contains(Character.toString((char)m.getNewColor())))
						inglP=inglP+Character.toString((char)m.getNewColor());
				}
			}
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaUnita.toString())) {
				if(m.getNewColor()==0) {
					if(!unita.contains(Character.toString((char)m.getNewColor())))
						unita=unita+Character.toString((char)m.getNewColor());
				}
				else {
					if(!unita.contains(Character.toString((char)m.getNewColor())))
						unita=unita+Character.toString((char)m.getNewColor());
				}
			}
			}
				
	
		
		lavoro.setAnteriore(ant);
		lavoro.setPosteriore(post);
		lavoro.setIngleseAnt(inglA);
		lavoro.setInglesePost(inglP);
		lavoro.setUnita(unita);
		lavoro.setRigaDisegno(rigaDisegno);
		lavoro.setGuidafilo(4);
		lavoro.setGradazione(5);
		lavoro.setSpostamento(0);
		lavoro.setTirapezza(5);
		
		return lavoro;
	}
	
	private void ricomponiRigaMatriceMaglia(ArrayList<Colore> maglieColori,Maglia[] rigaMatriceMaglia) {
		
		// aggiorno la riga delle matrice delle maglie 
		
		for(Colore c: maglieColori) {
			for(Maglia m: c.getAnteriore())
				rigaMatriceMaglia[m.getX()]=m;
			
			for(Maglia m: c.getPosteriore())
				rigaMatriceMaglia[m.getX()]=m;
			
			for(Maglia m: c.getInglAnt())
				rigaMatriceMaglia[m.getX()]=m;
			
			for(Maglia m: c.getInglPost())
				rigaMatriceMaglia[m.getX()]=m;
			
			for(Maglia m: c.getUnita())
				rigaMatriceMaglia[m.getX()]=m;
		}
		}
	
	private void riealoboraColoriAndMaglie(ArrayList<String> colors, ArrayList<Maglia> maglie) {
		
		
		String newColorAnt="";
		String newColorPost="";
		String newColorInglAnt="";
		String newColorInglPost="";
		String newColorUnita="";
		int newColor = 0;
		
		for(Maglia m:maglie) {
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
				
				if(newColorAnt.length()<1) {
					//genera nuovo colore
					newColor=Utility.getNextFreeColor(colors);
					newColorAnt=Character.toString((char)newColor);
					m.setNewColor(newColor);
					colors.add(newColorAnt);
				}
				else {
					m.setNewColor(newColor);
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
				if(newColorPost.length()<1) {
					//genera nuovo colore
					 newColor=Utility.getNextFreeColor(colors);
					newColorPost=Character.toString((char)newColor);
					m.setNewColor(newColor);
					colors.add(newColorPost);
				}
				else {
					m.setNewColor(newColor);
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaIngleseAnteriore.toString())) {
				if(newColorInglAnt.length()<1) {
					//genera nuovo colore
					 newColor=Utility.getNextFreeColor(colors);
					newColorInglAnt=Character.toString((char)newColor);
					m.setNewColor(newColor);
					colors.add(newColorInglAnt);
				}
				else {
					m.setNewColor(newColor);
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaInglesePosteriore.toString())) {
				if(newColorInglPost.length()<1) {
					//genera nuovo colore
					 newColor=Utility.getNextFreeColor(colors);
					newColorInglPost=Character.toString((char)newColor);
					m.setNewColor(newColor);
					colors.add(newColorInglPost);
				}
				else {
					m.setNewColor(newColor);
				}
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaUnita.toString())) {
				if(newColorUnita.length()<1) {
					//genera nuovo colore
					 newColor=Utility.getNextFreeColor(colors);
					newColorUnita=Character.toString((char)newColor);
					m.setNewColor(newColor);
					colors.add(newColorUnita);
				}
				else {
					m.setNewColor(newColor);
				}
			}
		}
	}

}
