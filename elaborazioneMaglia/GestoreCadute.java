package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GestoreCadute {

	private int nrCadute = 1;
	private int posizioneCarro=1; // il carro parte a sinistra
	private HashMap<String, String> posizioneGuidafilo;

	public GestoreCadute() {
		posizioneGuidafilo = new HashMap<>();
		for (int i = 1; i < 9; i++)
			posizioneGuidafilo.put(i + "", "SX"); // inizializzo i guidafilo tutti a sinistra
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
			ArrayList<TrasportoCaduta> righeTrasporto) {
		Caduta c;
		ArrayList<Caduta> lavoro = new ArrayList<>();
		HashMap mapTrasporti = getHashFromList(righeTrasporto);
		for (LavoroCaduta l : righeLavoro) {
			c = new Caduta();
			c.setLavoroA(l.getAnteriore());
			c.setLavoroP(l.getPosteriore());
			c.setRigaDisegno(l.getRigaDisegno());
			c.setTrasporto(false);
			c.setGradazione(l.getGradazione());
			c.setGuidafilo(l.getGuidafilo());
			c.setVelocita(l.getVelocita());
			c.setTirapezza(l.getTirapezza());
			lavoro.add(c);
			// verifico se vanno fatti dei trasporti
			if (mapTrasporti.containsKey(l.getRigaDisegno())) { // verificare override
				System.out.println("Ho gestito un trasporto in automatico");
				c = new Caduta();
				TrasportoCaduta tr = (TrasportoCaduta) mapTrasporti.get(l.getRigaDisegno());
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

	public String trasformaComandiMacchina(ArrayList<Caduta> lavoro) {
		String comando = "";
		String comandi ="";
		
		if (nrCadute == 1) {
			
			for (Caduta c : lavoro) {
				comando = "";
				boolean completo = false;
				if (!c.isTrasporto()) {
					// Devo lavorare
					String guidafilo = c.getGuidafilo() + "";
					String posizione = posizioneGuidafilo.get(guidafilo);

					while (!completo) {
						// carro SX <- DX con guidafilo a DX
						if (posizioneCarro % 2 == 0 && posizione.equalsIgnoreCase("DX")) {
							
								// posizione giusta x lavorare
								comando = vaiSinistraLavoro(c);
								invertiCarro();
								completo = true;
								posizione ="SX";
								posizioneGuidafilo.put(guidafilo, posizione);
								
							// Carro SX <- DX con guidafilo a SX
						} else if (posizioneCarro % 2 == 0 && posizione.equalsIgnoreCase("SX")){
							comando = "<< S0;";
							invertiCarro();
						}
						// Carro SX -> DX con guidafilo a SX
						else if(posizioneCarro % 2 != 0 && posizione.equalsIgnoreCase("SX")){
							
							comando = vaiDestraLavoro(c);
							invertiCarro();
							completo = true;
							posizione ="DX";
							posizioneGuidafilo.put(guidafilo, posizione);
						}
						// Carro SX -> DX con guidafilo a DX
						else if(posizioneCarro % 2 != 0 && posizione.equalsIgnoreCase("DX")){
							
							comando = ">> S0;";
							invertiCarro();
						}
						System.out.println(comando);
						comandi= comandi+"\n"+comando;
					}
				} else {
					// devo trasportare
					// Carro SX -> DX
					if(posizioneCarro % 2 != 0) {
						comando=vaiDestraTrasporta(c);
						invertiCarro();
						completo = true;
					}
					// Carro SX <- DX
					else if(posizioneCarro % 2 == 0) {
						comando=vaiSinistraTrasporta(c);
						invertiCarro();
						completo = true;
					}
					System.out.println(comando);
					comandi= comandi+"\n"+comando;
				}
			}
		} else if (nrCadute == 2) {
		} else if (nrCadute == 3) {
		}

		return comandi;
	}
	
	private void invertiCarro() {
		if(posizioneCarro==1)
			posizioneCarro=0;
		else
			posizioneCarro=1;
	}
	
	private String vaiDestraTrasporta(Caduta c) {
		
		String aD="";
		String dA="";
		if(c.getTrasportoAD().length()>0) {
			aD="TR, [] ("+c.getTrasportoAD()+")";
		}
		if(c.getTrasportoDA().length()>0) {
			dA=" /TR, [] ("+c.getTrasportoDA()+")";
		}
		
		String comando=">> I= "+aD+dA+ ", CM= TPZ"+ c.getTirapezza() + " VEL" + c.getVelocita() + " SP>0, S1;";
		return comando;
	}
	
	private String vaiSinistraTrasporta(Caduta c) {
		String aD="";
		String dA="";
		if(c.getTrasportoAD().length()>0) {
			aD="TR, [] ("+c.getTrasportoAD()+")";
		}
		if(c.getTrasportoDA().length()>0) {
			dA=" /TR, [] ("+c.getTrasportoDA()+")";
		}
		
		String comando="<< I= "+aD+dA+ ", CM= TPZ"+ c.getTirapezza() + " VEL" + c.getVelocita() + " SP>0, S1;";
		return comando;
	}

	private String vaiDestraLavoro(Caduta c) {
		String a = "";
		String p = "";
		if (c.getLavoroA().length() > 0)
			a = "LA,[" + c.getLavoroA() + "]";

		if (c.getLavoroP().length() > 0)
			p = " / LA,[" + c.getLavoroP() + "], ";

		String comando = ">> I= " + a + p + "GF=" + c.getGuidafilo() + ", CM= GRAD" + c.getGradazione() + " TPZ"
				+ c.getTirapezza() + " VEL" + c.getVelocita() + " SP>0, S1;";
		return comando;
	}

	private String vaiSinistraLavoro(Caduta c) {
		String a = "";
		String p = "";
		if (c.getLavoroA().length() > 0)
			a = "LA,[" + c.getLavoroA() + "]";

		if (c.getLavoroP().length() > 0)
			p = " / LA,[" + c.getLavoroP() + "], ";

		String comando = "<< I= " + a + p + "GF=" + c.getGuidafilo() + ", CM= GRAD" + c.getGradazione() + " TPZ"
				+ c.getTirapezza() + " VEL" + c.getVelocita() + " SP>0, S1;";
		return comando;
	}

}
