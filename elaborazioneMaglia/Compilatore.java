package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import Comandi.Comando;
import Inizio.Balza;
import jdraw.data.Clip.PUNTO_MAGLIA;
import jdraw.data.Palette;
import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;
import util.Util;

public class Compilatore {
	
	private Maglia[][] matriceMaglia;
	private Comando[][] matriceComandi;
	private static HashMap <Integer,String> tabellaColori;
	
	private final static int TRASPORTO_AVANTI_DIETRO=1;
	private final static int TRASPORTO_DIETRO_AVANTI=2;
	private final static int NO_TRASPORTO=3;
	
	
	private Balza balza;
	
	public Compilatore(Maglia[][] matriceMaglia,Comando[][] matriceComandi,Balza balza) {
		this.matriceMaglia= matriceMaglia;
		this.matriceComandi=matriceComandi;
		this.balza=balza;
		tabellaColori = Palette.getTabellaColoriMaglia();
		// capovolgendo la matrice ho il disegno in ordine di istruzioni partendo dal basso, quindi da 1
		Maglia [][] matriceMaglieTmp = Utility.capovolgiMatrice(matriceMaglia);
		elabora(matriceMaglieTmp,this.matriceComandi,this.balza);
	}
	
	public String getProgrammaElaborato() {
		return "";
	}
	
	
	private void elabora(Maglia[][] matriceMaglia,Comando[][] matriceComandi,Balza balza) {
		

		// ritorna la lista dei trasporti da fare, che devono essere ancora inseriti fra due righe di lavoro
		ArrayList<Trasporto> righeTrasporto = listraTrasportiDaFare(matriceMaglia);
		System.out.println("Trasporti automatici da fare individuati con successo");
		
		ArrayList<LavoroCaduta> lavoroAndTrasporti = null;
		// verifico se sono stati individuati dei trasporti. In caso di esito positivo li aggiungo alla matrice
		if(righeTrasporto.size()>0) {
			lavoroAndTrasporti=unisciTrasportoALavoro(matriceMaglia,righeTrasporto,matriceComandi);
			}
		else {
			lavoroAndTrasporti=inserisciSoloLavoro(matriceMaglia,matriceComandi);
		}
		
		//inserisco trasporto balza se esiste
		Trasporto tBalza;
		ArrayList<LavoroCaduta> lavoroTrasportiAndBalza = null;
		if(balza!=null ) {
			tBalza=elaboraTrasportoBalza(balza,lavoroAndTrasporti,matriceMaglia.length);
			lavoroTrasportiAndBalza=inserisciTrasportoBalza(tBalza,lavoroAndTrasporti);
			}
		
		
		// ritorna un array di oggetti LavoroCaduta con tutti i parametri necessari per ogni caduta (tipo di lavoro,gradazione,celoc,tirapezza,GF)
//		ArrayList<LavoroCaduta> righeLavoro = creaComandiLavoro(matriceMaglia,matriceComandi,balzaAttiva);
//		System.out.println("Comandi lavoro creati");
		
		
		// stampo sulla console e sul file la struttura della matrice disegno
		creaFileStruttura(getGuidafili(matriceComandi),lavoroTrasportiAndBalza,balza);
		System.out.println("File struttura creato");
		
		
		GestoreCadute gestore = new GestoreCadute();
		ArrayList<String>programma=gestore.creaComandiMacchina(lavoroTrasportiAndBalza);
		
		programma.size();
		//ArrayList<Caduta> lavoro =gestore.elaborazioneFinale2(lavoroAndTrasporti);
		System.out.println("Corse gestite correttamente");
		
//		String comandiMacchina= gestore.trasformaComandiMacchina(lavoro);
//		//creaFileComandi(comandiMacchina);
//		System.out.println("File comandi generato correttamente");
		
		
	}
	

	private ArrayList<LavoroCaduta> inserisciSoloLavoro(Maglia[][] matriceMaglia, Comando[][] matriceComandi) {
		// ci troviamo nel caso in cui non ci sono trasporti da fare nel disegno
		
		int nr=matriceMaglia.length;
		int nc = matriceMaglia[0].length;
		
		
		ArrayList<LavoroCaduta> soloLavoro = new ArrayList<>();
		
		for(int r=0;r<nr;r++) {
			LavoroCaduta caduta= new LavoroCaduta();
			//riassegno un colore diverso a ciascn tipo di lavoro in modo da poter disegnare sulla GUI con lo stesso colore
			Maglia[] maglieDivise=dividiLetterePerLavoro(matriceMaglia[r]);
			caduta.addMaglieAndSepara(maglieDivise); // aggiungo tutte le maglie in un arrayList
			caduta.addComandi(matriceComandi[r]); // aggiungo tutti i comandi in un arraylist
			caduta.setRigaDisegno(r);
			caduta.setSpostamento(0);
			soloLavoro.add(caduta);
			
			
		}
		
		return soloLavoro;
	}

	private ArrayList<LavoroCaduta> inserisciTrasportoBalza(Trasporto tBalza, ArrayList<LavoroCaduta> lavoroAndTrasporti) {
		ArrayList<LavoroCaduta> lavoroTrasportiAndBalza = null;
		// verifico se effettivamente ci sono delle maglie da trasportare
		if(tBalza.getMaglieDaTrasportare().size()>0) {
			lavoroTrasportiAndBalza= new ArrayList<>();
			Maglia[] daTrasportare=tBalza.getRigaRicostruita();
			LavoroCaduta trasportoBalza= new LavoroCaduta();
			trasportoBalza.setTrasportoSempliceAggiunto(true);
			trasportoBalza.addMaglieAndSepara(daTrasportare);
			trasportoBalza.setRigaDisegno(0);
			lavoroTrasportiAndBalza.add(trasportoBalza);
			
			// aggiungo la riga alla lista che contiene gia tutto il disegno
			for(LavoroCaduta l: lavoroAndTrasporti) {
				l.setRigaDisegno(l.getRigaDisegno()+1);
				lavoroTrasportiAndBalza.add(l);
			}
			
		}
		
		return lavoroTrasportiAndBalza;
	}

	private Trasporto elaboraTrasportoBalza(Balza balza, ArrayList<LavoroCaduta> lavoroAndTrasporti,int larghezza) {
		// TODO Auto-generated method stub
		//compongo ultima riga
		Maglia[] ultimaRiga = new Maglia[larghezza];
		
		LavoroCaduta primaCadutaRigaDisegno=lavoroAndTrasporti.get(0);
		Maglia[] primaRigaDisegno = new Maglia[larghezza];
		primaCadutaRigaDisegno.getMaglie().toArray(primaRigaDisegno);
		
		if(balza.toString().equalsIgnoreCase("Costa 1X1")) {
			for(int i=0;i<larghezza;i++) {
				if(i%2==0) {
					Maglia m= new Maglia();
					m.setColore(1);
					m.setX(i);
					m.setTipoLavoro(TipoLavoroEnum.MagliaPosteriore.toString());
					ultimaRiga[i]=m;
					}
				else {
					Maglia m= new Maglia();
					m.setColore(2);
					m.setX(i);
					m.setTipoLavoro(TipoLavoroEnum.MagliaAnteriore.toString());
					ultimaRiga[i]=m;
				}
			}
		}
		
		//verifico se devo fare dei trasporti
		ArrayList<Maglia> trasAD;
		ArrayList<Maglia> trasDA;
		trasAD = new ArrayList<>();
		trasDA = new ArrayList<>();
		ArrayList<Trasporto> trasporti = new ArrayList<>();
		
		for(int i=0;i<larghezza;i++) {
			Maglia attuale=ultimaRiga[i];
			Maglia successiva =primaRigaDisegno[i];
			
			int tipoTrasporto=decidiTrasporto(attuale, successiva);
			
			switch (tipoTrasporto) {
			case TRASPORTO_AVANTI_DIETRO: {
				trasAD.add(attuale);
			}
				
				break;
			case TRASPORTO_DIETRO_AVANTI: {
				trasDA.add(attuale);
			}
				
				break;
			case NO_TRASPORTO: {}
			
			break;

			default:
				break;
			}
			
		}
		Trasporto t = null;
		if(trasAD.size()>0 || trasDA.size()>0) {
			t = new Trasporto();
			t.setLarghezzaMatrice(larghezza);
			t.setRigaDisegno(0);
			t.addTrasporti(trasAD, trasDA);
			trasporti.add(t);
			}
		
		return t;
	}

	private ArrayList<LavoroCaduta> unisciTrasportoALavoro(Maglia[][] matriceMaglia,ArrayList<Trasporto> righeTrasporto,Comando[][] matriceComandi) {
		
		// questo metodo recupera la lista di tutti i trasporti da fare e li inserisci fra due righe lavoro.
		//Viene quindi creata una nuova matrice che contiene questa volta delle righe disegno ad hoc per i trasporti
		
		int nr=matriceMaglia.length;
		int nc = matriceMaglia[0].length;
		
		ArrayList<LavoroCaduta> lavoroAndTrasporti = new ArrayList<>();
		
		for(int r=0;r<nr;r++) {
			LavoroCaduta caduta= new LavoroCaduta();
			//riassegno un colore diverso a ciascn tipo di lavoro in modo da poter disegnare sulla GUI con lo stesso colore
			Maglia[] maglieDivise=dividiLetterePerLavoro(matriceMaglia[r]);
			caduta.addMaglieAndSepara(maglieDivise); // aggiungo tutte le maglie in un arrayList
			caduta.addComandi(matriceComandi[r]); // aggiungo tutti i comandi in un arraylist
			caduta.setRigaDisegno(r);
			caduta.setSpostamento(0);
			lavoroAndTrasporti.add(caduta);
			
			// avvio la verifica se effettuare un trasporto fra la riga corrente e quella successiva
			Trasporto t=checkTrasporto(r,righeTrasporto);
			if(t!=null) {
				// IL trasporto va fatto
				LavoroCaduta cadutaTrasporto= new LavoroCaduta();
				// la riga aggiunta viene ricostruita con tutte le maglia...quelle che non devo trasportare hanno il .
				cadutaTrasporto.addMaglieAndSepara(t.getRigaRicostruita());
				cadutaTrasporto.setTrasportoSempliceAggiunto(true);
				cadutaTrasporto.setRigaDisegno(r);
				cadutaTrasporto.setSpostamento(1);
				lavoroAndTrasporti.add(cadutaTrasporto);
			}
			
		}
		
		return lavoroAndTrasporti;
		
	}		
			
		
	
	
	private Maglia[] dividiLetterePerLavoro(Maglia[] maglia) {
		
		ArrayList<Maglia> maglieAnteriori= new ArrayList<>();
		ArrayList<Maglia> magliePosteriori = new ArrayList<>();
		ArrayList<String> coloriRiga= new ArrayList<>();
		
		Maglia []maglieNewColor = new Maglia[maglia.length];
		
		for(int i=0;i<maglia.length;i++) {
			String tipoLavoro= maglia[i].getTipoLavoro();
		if(tipoLavoro.equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
			maglieAnteriori.add(maglia[i]);
			String colore=getLetteraColoreFromMaglia(maglia[i].getColore());
			if(Utility.checkContainsColors(coloriRiga, colore)) {
				coloriRiga.add(colore);
			}
		}
		else if(tipoLavoro.equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
			magliePosteriori.add(maglia[i]);
			String colore=getLetteraColoreFromMaglia(maglia[i].getColore());
			if(Utility.checkContainsColors(coloriRiga, colore)) {
				coloriRiga.add(colore);
			}
			}
		}
		int newColor=Utility.getNextFreeColor(coloriRiga);
		String newColorChar=Utility.getASCIfromNumber(newColor);
		coloriRiga.add(newColorChar);
		
		// implemetare anche per la maglia unita
		
		for(Maglia m: magliePosteriori) {
			m.setNewColor(newColor);
		}
		
		for(int i=0; i<maglia.length;i++) {
			maglieNewColor[i]=Utility.getMagliaAtIndex(maglieAnteriori,magliePosteriori,i);
		}
		return maglieNewColor;
	}

	private Trasporto checkTrasporto(int r,ArrayList<Trasporto> righeTrasporto) {
		for(Trasporto t:righeTrasporto) {
			if(t.getRigaDisegno()==r)
				return t;
		}
		return null;
	}

	private ArrayList<Trasporto> listraTrasportiDaFare(Maglia[][] matriceMaglia) {
		
		int nr = matriceMaglia.length;
		int nc = matriceMaglia[0].length;
		
		int rigaDisegno=0;
		
		ArrayList<Maglia> trasAD;
		ArrayList<Maglia> trasDA;
		ArrayList<Trasporto> trasporti = new ArrayList<>();
		
		
		//tolgo una riga xk l'ultima di sicuro non devo fare trasporti
			for(int r=0;r<nr-1;r++) {
				trasAD = new ArrayList<>();
				trasDA = new ArrayList<>();
				for(int c=0;c<nc;c++) {
					Maglia attuale = null;
					Maglia successiva = null;
					attuale = matriceMaglia[r][c];
					successiva = matriceMaglia[r+1][c];
					int tipoTrasporto = decidiTrasporto(attuale,successiva);
					
					switch (tipoTrasporto) {
					case TRASPORTO_AVANTI_DIETRO: {
						trasAD.add(attuale);
					}
						
						break;
					case TRASPORTO_DIETRO_AVANTI: {
						trasDA.add(attuale);
					}
						
						break;
					case NO_TRASPORTO: {}
					
					break;

					default:
						break;
					}
				}
				
				if(trasAD.size()>0 || trasDA.size()>0) {
				Trasporto t = new Trasporto();
				t.setLarghezzaMatrice(nc);
				t.setRigaDisegno(rigaDisegno);
				t.addTrasporti(trasAD, trasDA);
				trasporti.add(t);
				}
				rigaDisegno++;
			}
		return trasporti;
	}

	private void inserisciRigheTrasportoMatrice(ArrayList<ArrayList<Maglia>> righe, ArrayList<Maglia> trasAD,ArrayList<Maglia> trasDA, int r) {
		// TODO Auto-generated method stub
		
	}

	public static int decidiTrasporto(Maglia attuale, Maglia successiva) {
		if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()) &&
				successiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
			return TRASPORTO_DIETRO_AVANTI;
		}
		else if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString()) &&
				successiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()))
			return TRASPORTO_AVANTI_DIETRO;
		else
			return NO_TRASPORTO;
	}

	private ArrayList<Maglia> aggiungiInizioBalza(Maglia[][] matriceMaglia, Balza balza) {
			// se è presente una balza... verifico se devo far i traporti prima di iniziare il lavoro
			ArrayList<Maglia>ultimaRigaBalza= new ArrayList<>();
			int nr = matriceMaglia.length;
			int nc = matriceMaglia[0].length;
			
			for(int i=0; i<nc;i++) {
				Maglia m;
				if(balza.toString().equalsIgnoreCase("Costa 1X1")) {
				if(i%2==1)
					m=new Maglia(i, nr, 1, TipoLavoroEnum.MagliaAnteriore.toString());
				else
					m=new Maglia(i, nr, 1, TipoLavoroEnum.MagliaPosteriore.toString());
				ultimaRigaBalza.add(m);
				}
			}
			
			
			
		return ultimaRigaBalza;
	}

	private ArrayList<Integer> getGuidafili(Comando[][] matriceComandi) {
		ArrayList<Integer> guidafili= new ArrayList<>();
		
		for(int i=0;i<matriceComandi.length;i++) {
			if(!guidafili.contains(Integer.parseInt(matriceComandi[i][1].getValue()))) {
				guidafili.add(Integer.parseInt(matriceComandi[i][1].getValue()));
			}
		}
		
		return guidafili;
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
		
		for(int r=0;r< nr; r++) {	
			String traspAD = "";
			String traspDA = "";
			String coloreA="";
			String coloreD="";
			ArrayList<Maglia> ad= new ArrayList<>();
			ArrayList<Maglia> da= new ArrayList<>();
			ArrayList<String> colorsA = new ArrayList<>();
			ArrayList<String> colorsD = new ArrayList<>();
			ArrayList<String> colors = new ArrayList<>();
			ArrayList<Integer> colorsInt = new ArrayList<>();
			
			for(int c =0; c < nc; c++) {
				Maglia attuale = null;
				Maglia rigaSccessiva = null;
				 attuale = matriceMaglia[r][c];
				 rigaSccessiva = matriceMaglia[r+1][c];
				// Trasporta da avanti a dietro
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
					ad.add(attuale);
					if(attuale.getNewColor()==0) {
						String tempcolor=getLetteraColoreFromMaglia(attuale.getColore());
						if(!coloreA.contains(tempcolor)) {
							colorsA.add(tempcolor);
							colors.add(tempcolor);
							colorsInt.add(attuale.getColore());
							coloreA=coloreA+tempcolor;
							}
					}
					else {
						String tempcolor=Utility.getASCIfromNumber(attuale.getNewColor());
						if(!coloreA.contains(tempcolor)) {
							colorsA.add(tempcolor);
							colors.add(tempcolor);
							colorsInt.add(attuale.getNewColor());
							coloreA=coloreA+tempcolor;
						}
					}
//					colore = (attuale.getNewColor()==0 ? getLetteraColoreFromMaglia(attuale.getColore()) : Character.toString((char)attuale.getNewColor()));
//					if(!traspAD.contains(colore)) {
//					traspAD=traspAD+colore;
//					}
				}
				
				// trasporto dietro avanti
				
				if(attuale.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString()) &&
						rigaSccessiva.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
					da.add(attuale);
					if(attuale.getNewColor()==0) {
						String tempcolor=getLetteraColoreFromMaglia(attuale.getColore());
						if(!coloreD.contains(tempcolor)) {
							colorsD.add(tempcolor);
							colors.add(tempcolor);
							colorsInt.add(attuale.getColore());
							coloreD=coloreD+tempcolor;
						}
					}
					else {
						String tempcolor=Utility.getASCIfromNumber(attuale.getNewColor());
						if(!coloreD.contains(tempcolor)) { 
							colorsD.add(tempcolor);
							colors.add(tempcolor);
							colorsInt.add(attuale.getNewColor());
							coloreD=coloreD+tempcolor;
						}
					}
				}
					else {
						// caso in cui non devo trasportare
						// aggiungo semplicemente il colore per tenerne traccia quando andro a recuperare dei nuovi colori
						if(attuale.getNewColor()==0) {
							String lettera=getLetteraColoreFromMaglia(attuale.getColore());
							if(Utility.checkContainsColors(colors, lettera))
								colors.add(lettera);
						}
						else {
							String lettera=Utility.getASCIfromNumber(attuale.getNewColor());
							if(Utility.checkContainsColors(colors, lettera))
								colors.add(lettera);
						}
						
					}
				}
			
			
			// assegno i nuovi colori
			if(ad.size()>0 || da.size()>0) {
				ArrayList<Integer> nuoviColori;
				
				
				if(da.size()>0) {
					//in colors ci sono tutti i colori utilizzati su quella stecca 
					nuoviColori=Utility.getNextFreeXColor(colors, colorsD.size());
					
					//aggiungo i nuovi colori alla lista in modo che per i prossimi trasporti non considero liberi quei colore
					for(Integer i:nuoviColori) {
						String c=Utility.getASCIfromNumber(i);
						colors.add(c);
						//uso lo stesso ciclo anche per prendere i colori da usare x il traporto
						traspDA=traspDA+c;
					}
					
					Iterator itr=nuoviColori.iterator();
					for(String oldColore:colorsD) {
						int nuovoColore=(int) itr.next();
						for(Maglia m: da) {
							if(m.getNewColor()==0) {
								if(getLetteraColoreFromMaglia(m.getColore()).equalsIgnoreCase(oldColore)) {
									m.setNewColor(nuovoColore);
								}
							}
							else {
								if(Utility.getASCIfromNumber(m.getNewColor()).equalsIgnoreCase(oldColore)) {
									m.setNewColor(nuovoColore);
								}
							}
						}
					}
				}
				
				if(ad.size()>0) {
					//in colors ci sono tutti i colori utilizzati su quella stecca 
					nuoviColori=Utility.getNextFreeXColor(colors, colorsA.size());
					
					//prendo i colori da mettere nel traposto
					for(Integer i:nuoviColori) {
						String c=Utility.getASCIfromNumber(i);
						traspAD=traspAD+c;
					}
					Iterator itr=nuoviColori.iterator();
					for(String oldColore:colorsA) {
						int nuovoColore=(int) itr.next();
						for(Maglia m: ad) {
							if(m.getNewColor()==0) {
								if(getLetteraColoreFromMaglia(m.getColore()).equalsIgnoreCase(oldColore)) {
									m.setNewColor(nuovoColore);
								}
							}
							else {
								if(Utility.getASCIfromNumber(m.getNewColor()).equalsIgnoreCase(oldColore)) {
									m.setNewColor(nuovoColore);
								}
							}
						}
					}
				}
				
				
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

	private boolean creaFileStruttura(ArrayList<Integer> guidafili,ArrayList<LavoroCaduta> lavoroAndTrasporti, Balza balza) {
		File f = new File("Struttura.txt");
		try {
			PrintStream ps = new PrintStream(f);
			ps.println("###########################################################################");
			ps.println("#-------- Programma di generazione automatico by Raffaele Sanzari --------#");
			ps.println("###########################################################################\n\n");
			
			ps.println("#-------------------------Selezioni Aghi-----------------------------------#\n\n");
			
			
			int nr = lavoroAndTrasporti.size()-1;
			int righeDisegno=lavoroAndTrasporti.size();
			int nc = 0;
			int indiceDisegno= nr;
			
			
			for(int r=nr ;r>-1; r--) {
				String indiceNormalizzato= Utility.normalizzaIndiceDisegno(indiceDisegno+1);
				System.out.print(indiceNormalizzato+" = '");
				ps.print(indiceNormalizzato+" = '");
				String colore;
				ArrayList<Maglia> maglieRiga=lavoroAndTrasporti.get(r).getMaglie();
				nc=maglieRiga.size();
				for(Maglia m:maglieRiga) { 
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
			ps.println("MOT MOTINIZIO = D0001 (D0001 - "+Utility.normalizzaIndiceDisegno(righeDisegno)+")");
			ps.println("COINIZIO = MOTINIZIO;");
			ps.println("FRINIZIO = MEM051 : MEM053 1 (TINIZIO 0'.');");
			ps.println("#-----------------------------------------------------------------------------\n\n\n");
			ps.println("#------ INIZIO GENERAZIONE PARAMETRI PER GUIDAFILO -----------\n");
			String gtbase="";
			String ghbase="";
			String ghpark="";
			String gxbase="";
			String ghpinz="";
			String ghpkz="";
			for(Integer guidafilo:guidafili) {
				gtbase=gtbase+guidafilo+"A N TELI1, ";
				ghbase=ghbase+guidafilo+"A+HGF"+guidafilo+"A, ";
				ghpark=ghpark+guidafilo+"A+PGF"+guidafilo+"A, ";
				gxbase=gxbase+guidafilo+"A [MEM30"+guidafilo+"-MEM31"+guidafilo+"], ";
				ghpinz=ghpinz+guidafilo+"A+HPGF"+guidafilo+"A, ";
				ghpkz=ghpkz+guidafilo+"A+PPGF"+guidafilo+"A, ";
			}
			ps.println("GTBASE ="+gtbase.substring(0, gtbase.length()-2)+";");
			ps.println("GHBASE ="+ghbase.substring(0, ghbase.length()-2)+";");
			ps.println("GHPARK ="+ghpark.substring(0, ghpark.length()-2)+";");
			ps.println("GXBASE ="+gxbase.substring(0, gxbase.length()-2)+";");
			ps.println("GHPINZ ="+ghpinz.substring(0, ghpinz.length()-2)+";");
			ps.println("GHPKPZ ="+ghpkz.substring(0, ghpkz.length()-2)+";");
			ps.println("#--------------------------------------------------------------------------------");
			ps.println("#						SUBROUTINES");
			ps.println("#--------------------------------------------------------------------------------");
			insertSubroutineAvvio(ps,balza);
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
			for(Integer guidafilo:guidafili) {
			ps.println("SET HGF"+guidafilo+"A = AGOIT1 - MEM30"+guidafilo+";");
			ps.println("SET PGF"+guidafilo+"A = AGOFT1 - MEM31"+guidafilo+";");
			ps.println("SET HPGF"+guidafilo+"A = PASSPIN * 0;");
			ps.println("SET HPGF"+guidafilo+"A = DISTPIN + HPGF"+guidafilo+"A;");
			ps.println("SET HPGF"+guidafilo+"A = 0 - HPGF"+guidafilo+"A;");
			ps.println("SET PPGF"+guidafilo+"A = PASSPIN * 0;");
			ps.println("SET PPGF"+guidafilo+"A = DISTPIN + PPGF"+guidafilo+"A;");
			ps.println("SET PPGF"+guidafilo+"A =  AGHIMAC + PPGF"+guidafilo+"A;");
			}
			
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
	
	private void insertSubroutineAvvio(PrintStream ps, Balza balza) {
		
		if(balza!=null) {
			gfPinze(ps);
			ptSxCon(ps);
			balzaNoPettine(ps,balza);
			
			
		}
		
	}
	
	private void balzaNoPettine(PrintStream ps, Balza balza) {
		
		ps.println("#\n\n--------- Inizio Balza no-Pettine ---------#");
		ps.println("SUB BALZA");
		File f = balza.getF();
		try {
			String comando= FileUtils.readFileToString(f);
			ps.println(comando);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ps.println("ENDSUB;");
		
	}

	private void ptSxCon(PrintStream ps) {
		
		ps.println("\n\n#-------Avvio partenza da sinistra con pettine--------#");
		ps.println("SUB PTSXCON");
		ps.println("IF FINEPROG = 0\n" + 
				"    SET MESSAGGIO = 1;\n" + 
				"    SET STOPLATO = STOPLATO+2;\n" + 
				"ATTIVA FRSCARICO;\n" + 
				">> I= LA,[] - LA,[], GF= 0, CM=GRAD26 TZT26 VEL20 SP>0+99, S2 ;\n" + 
				"<< I= LA,[] - LA,[], GF= 0, CM=TZT27 TPO, S2 ;\n" + 
				">> I= LA,[] - LA,[], GF= 0, CM=TPO, S2 ;\n" + 
				"<< I= LA,[] - LA,[], GF= 0, CM=TPO, S2 ;\n" + 
				"ATTIVA FRINIZIO;\n" + 
				">> I= LA,[] - LA,[] / LA,[]R('.x..') - LA,[]R('...x'), GF= 0/ 8, CM=GRAD21 TZT21 VEL20 SP>0, S1 S2 ;\n" + 
				"<< I= LA,[]R('...x') - LA,[]R('.x..'), GF= 8, CM=SP>0, S1 ;\n" + 
				">> I= LA,[]R('.x') - LA,[]R('x.') / LA,[](*), GF= 1/ 8(1), CM=GRAD22 TZT22 VEL20 SP>0 PTA, S1 S2 ;\n" + 
				"<< I= LA,[](*) / LA,[]R('x.') - LA,[]R('.x'), GF= 8GHPINZ/ 1GHPINZ, S1 S2 ;\n" + 
				"ENDIF;\n" + 
				"IF FINEPROG = 1\n" + 
				"    SET MESSAGGIO = 0;\n" + 
				"    SET STOPLATO = 0;\n" + 
				"ENDIF;\n" + 
				"");
		ps.println("ENDSUB;");
		
	}

	private void gfPinze(PrintStream ps) {
		ps.println("\n\nSUB GFPINZE");
		ps.println("IF FINEPROG = 0");
		ps.println("		SET MESSAGGIO = 2;");
		ps.println("		SET STOP LATO = 4;");
		ps.println("		ATTIVA GHPARK;");
		ps.println(">> CM=GRAD26 TPZ26 VEL20 SP>0+99,S0");
		ps.println("<< I=LA,[](*) / LA,[](*), GF=1AGHIPINZ / 4AGHIPINZ,S1 S1;");
		ps.println(">> SO;");
		ps.println("<< I=LA,[](*), GF= 8AGHPINZ, S1;");
		ps.println("ATTIVA GHPINZ;");
		ps.println("ENDIF;");
		ps.println("IF FINEPROGR = 1");
		ps.println("		SET MESSAGGIO = 0;");
		ps.println("		SET STOPLATO = 0;");
		ps.println("ENDIF;");
		ps.println("ENDSUB;");
	}
	
	private ArrayList<LavoroCaduta> creaComandiLavoro(Maglia[][] matriceMaglia,Comando[][] matriceComandi,boolean balza) {
		ArrayList<LavoroCaduta> righeLavoro = new ArrayList<>();
			int nr = matriceMaglia.length-1;
			
			for(int i=0;i< nr; i++) {
				LavoroCaduta lavoro =getMaglieLavoro(matriceMaglia[i], i,matriceComandi[i],balza);
				righeLavoro.add(lavoro);
			}
		
		return righeLavoro;
	}

	public static String getLetteraColoreFromMaglia(int colore) {
		// TODO Auto-generated method stub
		return tabellaColori.get(colore);
	}
	
	private LavoroCaduta getMaglieLavoro(Maglia[] rigaMatriceMaglia, int rigaDisegno, Comando[] rigaComandi,boolean balza) {
		String colore ="";
		ArrayList<String> colors = new ArrayList<>();
		String ant = "";
		String post ="";
		String inglA ="";
		String inglP = "";
		String unita = "";
		int tirapezza=1;
		int velocita=1;
		int gradazione=1;
		int guidafilo=1;
		
		
		if(!(rigaDisegno==0 && balza)) {
		// recupero i valori dei comandi dalle barra laterale per associarli alla stecca di disegno
		for(Comando c:rigaComandi) {
			if(c!=null && c.getComando().equalsIgnoreCase("Guidafilo")) {
				guidafilo=Integer.parseInt(c.getValue());
			}
			else if(c!=null &&c.getComando().equalsIgnoreCase("Gradazione")) {
				gradazione=Integer.parseInt(c.getValue());
			}
			else if(c!=null &&c.getComando().equalsIgnoreCase("Velocita")) {
				velocita=Integer.parseInt(c.getValue());
			}
			else if(c!=null &&c.getComando().equalsIgnoreCase("Tirapezza")) {
				tirapezza=Integer.parseInt(c.getValue());
			}
		}
		}
		
		
		ArrayList<Colore> colori = new ArrayList<>();
		LavoroCaduta lavoro = new LavoroCaduta();
		//recupero tutti i colori utilizzati in quella riga di disegno
		for(Maglia m: rigaMatriceMaglia) {
			if(!colore.contains(getLetteraColoreFromMaglia(m.getColore()))) {
				colore= colore + getLetteraColoreFromMaglia(m.getColore());
				colors.add(getLetteraColoreFromMaglia(m.getColore()));
			}
		}
			
		// questo mi permette di utilizzare lo stesso colore per lavorare in più modi
			for(String col : colors) {
				// scorro la riga per ogni colore; Per ogni colore verifico che tipo di lavoro devo fare
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
				// per ogni colore verifico se devo riealborare le maglie; rielaboro se con lo stesso colore devo lavorare sia avanti che dietro
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
		lavoro.setGuidafilo(guidafilo);
		lavoro.setGradazione(gradazione);
		lavoro.setSpostamento(0);
		lavoro.setTirapezza(tirapezza);
		lavoro.setVelocita(velocita);
		
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
