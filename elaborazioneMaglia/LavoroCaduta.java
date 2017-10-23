package elaborazioneMaglia;

import java.util.ArrayList;

import Comandi.Comando;
import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;

public class LavoroCaduta {
	
	private int caduta;
	private String anteriore;
	private String posteriore;
	private String ingleseAnt;
	private String inglesePost;
	private String unita;
	
	private int velocita;
	private int gradazione;
	private int spostamento;
	
	private int tirapezza;
	
	private int guidafilo;
	
	private int rigaDisegno;
	
	private boolean trasportoSempliceAggiunto=false;
	
	private ArrayList<Maglia> maglie;
	private ArrayList<Comando> comandi;
	
	public LavoroCaduta() {
		anteriore="";
		posteriore="";
		maglie = new ArrayList<>();
		comandi = new ArrayList<>();
	}

	public int getCaduta() {
		return caduta;
	}

	public void setCaduta(int caduta) {
		this.caduta = caduta;
	}

	public String getAnteriore() {
		return anteriore;
	}

	public void setAnteriore(String anteriore) {
		this.anteriore = anteriore;
	}

	public String getPosteriore() {
		return posteriore;
	}

	public void setPosteriore(String posteriore) {
		this.posteriore = posteriore;
	}

	public String getIngleseAnt() {
		return ingleseAnt;
	}

	public void setIngleseAnt(String ingleseAnt) {
		this.ingleseAnt = ingleseAnt;
	}

	public String getInglesePost() {
		return inglesePost;
	}

	public void setInglesePost(String inglesePost) {
		this.inglesePost = inglesePost;
	}

	public int getVelocita() {
		return velocita;
	}

	public void setVelocita(int velocita) {
		this.velocita = velocita;
	}

	public int getGradazione() {
		return gradazione;
	}

	public void setGradazione(int gradazione) {
		this.gradazione = gradazione;
	}

	public int getSpostamento() {
		return spostamento;
	}

	public void setSpostamento(int spostamento) {
		this.spostamento = spostamento;
	}

	public int getRigaDisegno() {
		return rigaDisegno;
	}

	public void setRigaDisegno(int rigaDisegno) {
		this.rigaDisegno = rigaDisegno;
	}

	public int getGuidafilo() {
		return guidafilo;
	}

	public void setGuidafilo(int guidafilo) {
		this.guidafilo = guidafilo;
	}

	public int getTirapezza() {
		return tirapezza;
	}

	public void setTirapezza(int tirapezza) {
		this.tirapezza = tirapezza;
	}

	public String getUnita() {
		return unita;
	}

	public void setUnita(String unita) {
		this.unita = unita;
	}

	public boolean isTrasportoSempliceAggiunto() {
		return trasportoSempliceAggiunto;
	}

	public void setTrasportoSempliceAggiunto(boolean trasportoSempliceAggiunto) {
		this.trasportoSempliceAggiunto = trasportoSempliceAggiunto;
	}
	
	public void addMaglie(Maglia[] maglietoAdd) {
		for(int i=0;i<maglietoAdd.length;i++) {
			maglie.add(maglietoAdd[i]);
		}
	}
	
	public void addMaglieAndSepara(Maglia[] maglietoAdd) {
		for(int i=0;i<maglietoAdd.length;i++) {
			maglie.add(maglietoAdd[i]);
			
			Maglia m=maglietoAdd[i];
			String colore;
			if(m.getNewColor()==0) {
				colore=Compilatore.getLetteraColoreFromMaglia(m.getColore());
			}else {
				colore=Utility.getASCIfromNumber(m.getNewColor());
			}
			
			if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaAnteriore.toString())) {
				if(!this.anteriore.contains(colore))
					this.anteriore=this.anteriore+colore;
			}
			else if(m.getTipoLavoro().equalsIgnoreCase(TipoLavoroEnum.MagliaPosteriore.toString())) {
				if(!this.posteriore.contains(colore))
					this.posteriore=this.posteriore+colore;
			}
			
		}
	}
	
	public void addComandi(Comando[] comanditoAdd) {
		for(int i=1;i<comanditoAdd.length;i++) {
			comandi.add(comanditoAdd[i]);
			switch (i) {
			case Comando.GRADAZIONE:
				this.gradazione=Integer.parseInt(comanditoAdd[i].getValue());
				break;
			case Comando.TIRAPEZZA:
				this.tirapezza=Integer.parseInt(comanditoAdd[i].getValue());
				break;
			case Comando.VELOCITA:
				this.velocita=Integer.parseInt(comanditoAdd[i].getValue());
				break;
			case Comando.GUIDAFILO:
				this.guidafilo=Integer.parseInt(comanditoAdd[i].getValue());
				break;

			default:
				break;
			}
		}
	}

	public ArrayList<Maglia> getMaglie() {
		return maglie;
	}

	public void setMaglie(ArrayList<Maglia> maglie) {
		this.maglie = maglie;
	}

	public ArrayList<Comando> getComandi() {
		return comandi;
	}

	public void setComandi(ArrayList<Comando> comandi) {
		this.comandi = comandi;
	}
	
	
	
	
	
	
	
	

}
