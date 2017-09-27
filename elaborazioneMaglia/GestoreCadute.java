package elaborazioneMaglia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GestoreCadute {
	
	
	private int nrCadute = 2;
	
	public GestoreCadute() {
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

	public ArrayList<Caduta> elaborazioneFinale(ArrayList<LavoroCaduta> righeLavoro, ArrayList<TrasportoCaduta> righeTrasporto) {
		Caduta c;
		ArrayList<Caduta> lavoro = new ArrayList<>();
		HashMap mapTrasporti =getHashFromList(righeTrasporto);
		for(LavoroCaduta l: righeLavoro) {
			c= new Caduta();
			c.setLavoroA(l.getAnteriore());
			c.setLavoroP(l.getPosteriore());
			c.setRigaDisegno(l.getRigaDisegno());
			c.setTrasporto(false);
			lavoro.add(c);
			// verifico se vanno fatti dei trasporti
			if(mapTrasporti.containsKey(l.getRigaDisegno())) {	// verificare override
				System.out.println("Ho gestito un trasporto in automatico");
				c= new Caduta();
				TrasportoCaduta tr=(TrasportoCaduta) mapTrasporti.get(l.getRigaDisegno());
				c.setTrasportoAD(tr.getAvantiDietro());
				c.setTrasportoDA(tr.getDietroAvanti());
				c.setPosizione(1);
				c.setTrasporto(true);
				lavoro.add(c);
			}
		}
		
		return lavoro;
	}
	
	private HashMap<Integer,TrasportoCaduta> getHashFromList(ArrayList<TrasportoCaduta> righeTrasporto){
		HashMap<Integer, TrasportoCaduta> map = new HashMap<Integer,TrasportoCaduta>();
		for(TrasportoCaduta t: righeTrasporto) {
			map.put(t.getRigaDisegno(), t);
		}
		return map;
	}



	public String trasformaComandiMacchina(ArrayList<Caduta> lavoro) {
		
		for(Caduta c:lavoro) {
			
		}
		return null;
	}
	
	
	private String getDestra() {
		return "";
	}
	
	private String getSinistra() {
		return "";
	}

}
