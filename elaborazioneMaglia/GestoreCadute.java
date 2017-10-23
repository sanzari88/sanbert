package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Comandi.Trasformatore;
import magliera.puntoMaglia.Maglia;

public class GestoreCadute {

	private int nrCadute = 2;
	private int posizioneCarro = 1; // il carro parte a sinistra
	
	private final int SINISTRA=1;
	private final int DESTRA=0;
	
	private HashMap<String, Integer> posizioneGuidafilo;

	public GestoreCadute() {
		posizioneGuidafilo = new HashMap<>();
		for (int i = 1; i < 9; i++)
			posizioneGuidafilo.put(i + "", SINISTRA); // inizializzo i guidafilo tutti a sinistra
	}

	public void elabora() {
		File f = new File("Struttura.txt");
		try {
			PrintStream ps = new PrintStream(f);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Caduta> elaborazioneFinale(ArrayList<LavoroCaduta> righeLavoro,
			ArrayList<TrasportoCaduta> righeTrasporto, boolean balza) {
		Caduta c;
		ArrayList<Caduta> lavoro = new ArrayList<>();
		HashMap mapTrasporti = getHashFromList(righeTrasporto);
		int i = 0;
		if (balza) {
			c = new Caduta();
			TrasportoCaduta tr = (TrasportoCaduta) mapTrasporti.get(righeLavoro.get(0).getRigaDisegno());
			c.setTrasportoAD(tr.getAvantiDietro());
			c.setTrasportoDA(tr.getDietroAvanti());
			c.setPosizione(1);
			c.setTrasporto(true);
			lavoro.add(c);
			System.out.println("Ho gestito un in automatico il trasporto per balza");
		}

		// for (LavoroCaduta l : righeLavoro) {
		for (; i < righeLavoro.size(); i++) {
			c = new Caduta();
			c.setLavoroA(righeLavoro.get(i).getAnteriore());
			c.setLavoroP(righeLavoro.get(i).getPosteriore());
			c.setRigaDisegno(righeLavoro.get(i).getRigaDisegno());
			c.setTrasporto(false);
			c.setGradazione(righeLavoro.get(i).getGradazione());
			c.setGuidafilo(righeLavoro.get(i).getGuidafilo());
			c.setVelocita(righeLavoro.get(i).getVelocita());
			c.setTirapezza(righeLavoro.get(i).getTirapezza());
			lavoro.add(c);
			// verifico se vanno fatti dei trasporti
			if (mapTrasporti.containsKey(righeLavoro.get(i).getRigaDisegno())) { // verificare override
				System.out.println("Ho gestito un trasporto in automatico");
				c = new Caduta();
				TrasportoCaduta tr = (TrasportoCaduta) mapTrasporti.get(righeLavoro.get(i).getRigaDisegno());
				c.setTrasportoAD(tr.getAvantiDietro());
				c.setTrasportoDA(tr.getDietroAvanti());
				c.setPosizione(1);
				c.setTrasporto(true);
				lavoro.add(c);
			}
		}

		return lavoro;
	}

	private HashMap<Integer, TrasportoCaduta> getHashFromList(ArrayList<TrasportoCaduta> righeTrasporto) {
		HashMap<Integer, TrasportoCaduta> map = new HashMap<Integer, TrasportoCaduta>();
		for (TrasportoCaduta t : righeTrasporto) {
			map.put(t.getRigaDisegno(), t);
		}
		return map;
	}


	public ArrayList<String> creaComandiMacchina(ArrayList<LavoroCaduta> lavoroTrasportiAndBalza) {

		ArrayList<String> comandi= new ArrayList<>();
		Trasformatore trasf= new Trasformatore();
		int penultimoComando = lavoroTrasportiAndBalza.size()-1;
		if (nrCadute == 2) {
			for (int i = 0; i < lavoroTrasportiAndBalza.size(); i++) {

				// LAVORO - LAVORO
				if (i!=penultimoComando && !lavoroTrasportiAndBalza.get(i).isTrasportoSempliceAggiunto() && !lavoroTrasportiAndBalza.get(i+1).isTrasportoSempliceAggiunto()) {// Lavoro-Lavoro
					LavoroCaduta primaC=lavoroTrasportiAndBalza.get(i);
					LavoroCaduta secondaCTemp=lavoroTrasportiAndBalza.get(i+1);
					boolean completo=false;
					int posGF1=posizioneGuidafilo.get(primaC.getGuidafilo()+"");
					int posGF2=0;
					if(posizioneGuidafilo.containsKey(secondaCTemp.getGuidafilo()+""))
					 posGF2=posizioneGuidafilo.get(secondaCTemp.getGuidafilo()+"");
					while(!completo) {
					if(posizioneCarro==SINISTRA && posGF1==SINISTRA && posGF2== SINISTRA) {
						//Sono nel caso buono per poter lavorare
						LavoroCaduta secondaC=lavoroTrasportiAndBalza.get(i+1);
						i++;
						aggiornaPosizioneGF(primaC.getGuidafilo());
						aggiornaPosizioneGF(secondaC.getGuidafilo());
						String comandoTemp=">>"+ trasf.vaiLavoro(primaC,secondaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
					}
					else if(posizioneCarro==SINISTRA && posGF1==DESTRA && posGF2== SINISTRA) {
						// devo aggiungere una corsa vuota
						comandi.add(">> S0;");
						System.out.println(">> S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
					else if(posizioneCarro==SINISTRA && posGF1==SINISTRA && posGF2 == DESTRA) {
						// procedo ad una caduta solo con il GF1 a sinistra
						aggiornaPosizioneGF(primaC.getGuidafilo());
						String comandoTemp=">>"+ trasf.vaiLavoro(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
						
						
					}
					else if(posizioneCarro==SINISTRA && posGF1==DESTRA && posGF2 == DESTRA) {
						// aggiungo una corsa vuota poichè non posso partire a lavorare con la seconda caduta
						comandi.add(">> S0;");
						System.out.println(">> S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
					else if(posizioneCarro==DESTRA && posGF1==DESTRA && posGF2== DESTRA) {
						// sono nel caso buono per lavora a 2cadute da destra
						LavoroCaduta secondaC=lavoroTrasportiAndBalza.get(i+1);
						i++;
						aggiornaPosizioneGF(primaC.getGuidafilo());
						aggiornaPosizioneGF(secondaC.getGuidafilo());
						String comandoTemp="<<"+ trasf.vaiLavoro(primaC,secondaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
					}
					else if(posizioneCarro==DESTRA && posGF1==SINISTRA && posGF2== DESTRA) {
						// Agiiungo una corsa vuota
						comandi.add("<< S0;");
						System.out.println("<< S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
					else if(posizioneCarro==DESTRA && posGF1==DESTRA && posGF2== SINISTRA) {
						// procedo con una sola caduta solo con il GF1
						aggiornaPosizioneGF(primaC.getGuidafilo());
						String comandoTemp="<<"+ trasf.vaiLavoro(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
					}
					else if(posizioneCarro==DESTRA && posGF1==SINISTRA && posGF2== SINISTRA) {
						// aggiungo una corsa vuota poichè non posso partire a lavorare con la seconda caduta
						comandi.add("<< S0;");
						System.out.println("<< S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
				}
				} else if (lavoroTrasportiAndBalza.get(i).isTrasportoSempliceAggiunto()) { // trasporto
					LavoroCaduta primaC=lavoroTrasportiAndBalza.get(i);
					if(posizioneCarro==DESTRA) {
						String comandoTemp="<< "+trasf.vaiTrasporto(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
					}
					else if(posizioneCarro==SINISTRA) {
						String comandoTemp=">> "+trasf.vaiTrasporto(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
					}
					
				} else if (i!=penultimoComando && !lavoroTrasportiAndBalza.get(i).isTrasportoSempliceAggiunto() && lavoroTrasportiAndBalza.get(i+1).isTrasportoSempliceAggiunto()) { // Lavoro
					LavoroCaduta primaC=lavoroTrasportiAndBalza.get(i);
					boolean completo=false;
					int posGF1=posizioneGuidafilo.get(primaC.getGuidafilo()+"");
					
					while(!completo) {
					if(posizioneCarro==SINISTRA && posGF1==SINISTRA) {
						
						aggiornaPosizioneGF(primaC.getGuidafilo());
						String comandoTemp=">>"+ trasf.vaiLavoro(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
					}
					else if(posizioneCarro==SINISTRA && posGF1==DESTRA) {
						// aggiungo una corsa vuota poichè ho il gf all altro lato
						comandi.add(">> S0;");
						System.out.println(">> S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
					else if(posizioneCarro==DESTRA && posGF1==SINISTRA) {
						// aggiungo una corsa vuota poichè ho il gf all altro lato
						comandi.add("<< S0;");
						System.out.println("<< S0;");
						aggiornaPosizioneCarro();
						completo=false;
					}
					else if(posizioneCarro==DESTRA && posGF1==DESTRA) {
						aggiornaPosizioneGF(primaC.getGuidafilo());
						String comandoTemp="<<"+ trasf.vaiLavoro(primaC);
						comandi.add(comandoTemp);
						System.out.println(comandoTemp);
						aggiornaPosizioneCarro();
						completo=true;
					}
					}
				}
			}
		}
		else if(nrCadute ==1) {}
		
		return comandi;
	}

	private void aggiornaPosizioneCarro() {
		if(posizioneCarro==1)
			posizioneCarro=0;
		else
			posizioneCarro=1;
		
	}
	
	private void aggiornaPosizioneGF(int gf) {
		int posizione =posizioneGuidafilo.get(gf+"");
		if(posizione==SINISTRA)
			posizioneGuidafilo.replace(gf+"", DESTRA);
		else
			posizioneGuidafilo.replace(gf+"", SINISTRA);
	}

}
