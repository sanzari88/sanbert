package magliera.puntoMaglia;

public class Maglia {
	
	private int x;
	
	private int y;
	
	private int colore;
	
	private String tipoLavoro;
	
	
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
	
	

}
