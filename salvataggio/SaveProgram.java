package salvataggio;

import java.io.Serializable;

import Comandi.Comando;
import jdraw.data.Clip;
import jdraw.data.Picture;
import jdraw.gui.Tool;
import magliera.puntoMaglia.Maglia;

public class SaveProgram implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Picture p;
	private Maglia[][] matrMaglia;
	private Comando [][] matrComandi;
	
	public SaveProgram(Picture p, Maglia[][] matrMaglia,Comando [][] matrComandi) {
		 this.p=p;
		 this.matrMaglia=matrMaglia;
		 this.matrComandi=matrComandi;
	}

	public Picture getP() {
		return p;
	}

	public void setP(Picture p) {
		this.p = p;
	}

	public Maglia[][] getMatriceMaglia() {
		return matrMaglia;
	}

	public void setC(Maglia[][] matrMaglia) {
		this.matrMaglia = matrMaglia;
	}

	public Comando[][] getMatrComandi() {
		return matrComandi;
	}

	public void setMatrComandi(Comando[][] matrComandi) {
		this.matrComandi = matrComandi;
	}
	
	
	
	
	

}
