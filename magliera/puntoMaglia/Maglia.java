package magliera.puntoMaglia;

import java.io.Serializable;

public class Maglia implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int x;
	
	private int y;
	
	private int colore;
	
	private int newColor =0;
	
	private String tipoLavoro;
	
	public Maglia() {}
	
	
	public Maglia(int x,int y, int colore, String tipoLavoro) {
		this.x = x;
		this.y = y;
		this.colore = colore;
		this.tipoLavoro = tipoLavoro;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int getColore() {
		return colore;
	}


	public void setColore(int colore) {
		this.colore = colore;
	}


	public String getTipoLavoro() {
		return tipoLavoro;
	}


	public void setTipoLavoro(String tipoLavoro) {
		this.tipoLavoro = tipoLavoro;
	}


	public int getNewColor() {
		return newColor;
	}


	public void setNewColor(int newColor) {
		this.newColor = newColor;
	}
	
	
	
	

}
