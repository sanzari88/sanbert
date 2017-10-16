package Comandi;

import java.io.Serializable;

public class Comando implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comando;
	private String value="";
	private int colore;
	
	public Comando() {}

	public String getComando() {
		return comando;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getColore() {
		return colore;
	}

	public void setColore(int colore) {
		this.colore = colore;
	}
	
	
	

}
