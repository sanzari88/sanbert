package elaborazioneMaglia;

import java.util.ArrayList;

import magliera.puntoMaglia.Maglia;
import magliera.puntoMaglia.TipoLavoroEnum;

public class Trasporto {
	
	private int rigaDisegno;
	
	private int larghezzaMatrice;
	
	private ArrayList<Maglia> maglieDaTrasportare;
	
	public Trasporto() {
		
		maglieDaTrasportare = new ArrayList<>();
	}
	
	public void addTrasporti(ArrayList<Maglia> ad,ArrayList<Maglia> da) {
		
		for(Maglia m:ad) {
			maglieDaTrasportare.add(m);
		}
		
		for(Maglia m:da) {
			maglieDaTrasportare.add(m);
		}
	}

	public int getRigaDisegno() {
		return rigaDisegno;
	}

	public void setRigaDisegno(int rigaDisegno) {
		this.rigaDisegno = rigaDisegno;
	}

	public ArrayList<Maglia> getMaglieDaTrasportare() {
		return maglieDaTrasportare;
	}

	public void setMaglieDaTrasportare(ArrayList<Maglia> maglieDaTrasportare) {
		this.maglieDaTrasportare = maglieDaTrasportare;
	}
	
	
	public Maglia [] getRigaRicostruita() {
		Maglia [] riga = new Maglia[larghezzaMatrice];
		
		for(int i=0;i<larghezzaMatrice;i++) {
			Maglia m=getMagliaAtIndex(i);
			if(m!=null) {
				riga[i]=m;
			}
			else {
			m=new Maglia();
			m.setNewColor(46); // 46 in ascii è il .
			m.setTipoLavoro(TipoLavoroEnum.No_Lavoro.toString());
			riga[i]=m;
			}
		}
	
		return riga;
	}

	private Maglia getMagliaAtIndex(int i) {
		
		for(Maglia m: this.maglieDaTrasportare) {
			if(m.getX()==i)
				return m;
		}
		return null;
	}

	public int getLarghezzaMatrice() {
		return larghezzaMatrice;
	}

	public void setLarghezzaMatrice(int larghezzaMatrice) {
		this.larghezzaMatrice = larghezzaMatrice;
	}
	
	
	
	

}
