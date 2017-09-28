package salvataggio;

import java.io.Serializable;

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
	private Maglia[][] c;
	
	public SaveProgram(Picture p, Maglia[][] c) {
		 this.p=p;
		 this.c=c;
	}

	public Picture getP() {
		return p;
	}

	public void setP(Picture p) {
		this.p = p;
	}

	public Maglia[][] getC() {
		return c;
	}

	public void setC(Maglia[][] c) {
		this.c = c;
	}
	
	
	

}
