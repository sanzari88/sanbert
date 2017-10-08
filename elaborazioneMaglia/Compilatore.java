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
		
		righeLavoro = creaComandiLavoro(matriceMaglia);
		System.out.println("Comandi lavoro creati");
		
		creaFileStruttura();
		System.out.println("File struttura creato");
		
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
			String colore="";
			
			for(int c =0; c < nc; c++) { 
				Maglia attuale = matriceMaglia[r][c];
				Maglia rigaSccessiva = matriceMaglia[r+1][c];
				// Trasporta da avanti a dietro
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
					colore = (attuale.getNewColor()==0 ? getLetteraColoreFromMaglia(attuale.getColore()) : Character.toString((char)attuale.getNewColor()));
					if(!traspAD.contains(colore)) {
					traspAD=traspAD+colore;
					}
				}
				
				// trasporto dietro avanti
				
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
					colore = (attuale.getNewColor()==0 ? getLetteraColoreFromMaglia(attuale.getColore()) : Character.toString((char)attuale.getNewColor()));
					if(!traspDA.contains(colore)) {
						traspDA=traspDA+colore;
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
			ps.println("###########################################################################");
			ps.println("#-------- Programma di generazione automatico by Raffaele Sanzari --------#");
			ps.println("###########################################################################\n\n\n\n");
			
			ps.println("#-------------------------Selezioni Aghi-----------------------------------#\n\n\n");
			
			
			int nr = matriceMaglia.length-1;
			int nc = matriceMaglia[0].length;
			int indiceDisegno= nr;
			
			
			for(int r=0 ;r<= nr; r++) {
				String indiceNormalizzato= Utility.normalizzaIndiceDisegno(indiceDisegno+1);
				System.out.print(indiceNormalizzato+" = '");
				ps.print(indiceNormalizzato+" = '");
				String colore;
				for(int i=0; i<nc; i++) { 
					 Maglia m = matriceMaglia[r][i];
					 if(m.getNewColor()==0) {
					  colore = getLetteraColoreFromMaglia(m.getColore());
					  ps.print(colore);
					  System.out.print(colore);
					 }
					 else {
						 colore = Character.toString((char)m.getNewColor());
						  ps.print(colore);
						  System.out.print(colore);
					 }
				}
				ps.println("';");
				System.out.println("';");
				indiceDisegno--;
			}
			ps.println("#------------------------- FINE  Selezioni Aghi-----------------------------#\n\n\n");
			ps.println("#	Inizio generazione parametri, immagliamento, motivi, composizioni  \n\n");
			ps.println("#	Teli \n\n");
			ps.println("TELO TELI = AGOIT1 - AGOFT1;");
			ps.println("MOT MOTINIZIO = D0001 (D0001 - "+Utility.normalizzaIndiceDisegno(nr)+")");
			ps.println("COINIZIO = MOTINIZIO;");
			ps.println("FRINIZIO = MEM051 : MEM053 (TINIZIO 1'.');");
			ps.println("#-----------------------------------------------------------------------------\n\n\n");
			ps.println("#------ INIZIO GENERAZIONE PARAMETRI PER GUIDAFILO -----------\n");
			ps.println("GTBASE =");
			ps.println("GHBASE =");
			ps.println("GHPARK =");
			ps.println("GXBASE =");
			ps.println("GHPINZ =");
			ps.println("GHPKPZ =");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#						SUBROUTINES");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#						ECONOMIZZATORI");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#						FASE ESECUTIVA");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("ESEGUI");
			ps.println("SET CORTISUP = MEM91;");
			ps.println("SET CORTIRAP1 = MEM92;");
			ps.println("SET CORTIRAP2 = MEM93;");
			ps.println("SET CORGRADAZ = MEM94;");
			ps.println("SET INCRMODE = 1;");
			ps.println("SET AGHITELO = "+nc+";");
			ps.println("SET AGHITOT = AGHITELO + MEM54;");
			ps.println("SET AGHITELO = AGHITOT * 0;");
			ps.println("SET AGOIT1 = MEM51 + AGHITELI;");
			ps.println("SET AGHITELO = AGHITELO + 1;");
			ps.println("SET AGHITELO = AGHITELO * MEM53;");
			ps.println("SET AGOFT1 = AGOIT1 + AGHITELO;");
			ps.println("SET DISTPIN = 9 * FINEZZA;");
			ps.println("SET PASSPIN = 110 * FINEZZA;");
			ps.println("SET PASSPIN = PASSPIN / 254;");
			
			ps.println("#--------    GESTIONE EZTRACORSA GUIDAFILO	-------");
			
			ps.println("SET HGF4A = AGOIT1 - MEM304;");
			ps.println("SET PGF4A = AGOFT1 - MEM314;");
			ps.println("SET HPGF4A = PASSPIN * 0;");
			ps.println("SET HPGF4A = DISTPIN + HPGF4A;");
			ps.println("SET HPGF4A = 0 - HPGF4A;");
			ps.println("SET PPGF4A = PASSPIN * 0;");
			ps.println("SET PPGF4A = DISTPIN + PPGF4A;");
			ps.println("SET PPGF4A =  AGHIMAC + PPGF4A;");
			
			ps.println("ATTIVA GTBASE;");
			ps.println("ATTIVA GXBASE;");
			ps.println("ATTIVA GHPINZ;");
			ps.println("ATTIVA FRINIZIO;");
			ps.println("GOSUB GFPINZE ;");
			ps.println("ATTIVA GHBASE;");
			ps.println("ATTIVA FRINIZIO;");
			
			ps.println("#--------    INIZIO COMANDI MACCHINA	-------");
			
			
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
					color=getLetteraColoreFromMaglia(m.getColore());
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
					color=getLetteraColoreFromMaglia(m.getColore());
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
					color=getLetteraColoreFromMaglia(m.getColore());
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
					color=getLetteraColoreFromMaglia(m.getColore());
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
					color=getLetteraColoreFromMaglia(m.getColore());
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
