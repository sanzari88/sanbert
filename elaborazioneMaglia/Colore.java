package elaborazioneMaglia;

import java.util.ArrayList;

import magliera.puntoMaglia.Maglia;

public class Colore {
	private ArrayList<Maglia> anteriore;
	private ArrayList<Maglia> posteriore;
	private ArrayList<Maglia> unita;
	private ArrayList<Maglia> inglPost;
	private ArrayList<Maglia> inglAnt;
	private String colore;
	
	
	public Colore() {
		anteriore= new ArrayList<>();
		posteriore= new ArrayList<>();
		unita= new ArrayList<>();
		inglPost= new ArrayList<>();
		inglAnt= new ArrayList<>();
	}
	
	public boolean isMaglieUgualiColore() {
		int count =0;
		if(!anteriore.isEmpty())
			count++;
		if(!posteriore.isEmpty())
			count++;
		if(!unita.isEmpty())
			count++;
		if(!inglPost.isEmpty())
			count++;
		if(!inglAnt.isEmpty())
			count++;
		if(count==1)
			return true;
		else
			return false;
	}
	
	public void addAnteriore(Maglia m) {
		anteriore.add(m);
	}
	
	public void addPosteriore(Maglia m) {
		posteriore.add(m);
	}
	
	public void addUnita(Maglia m) {
		unita.add(m);
	}
	
	public void addInglPost(Maglia m) {
		inglPost.add(m);
	}
	
	public void addInglAnt(Maglia m) {
		inglAnt.add(m);
	}

	public ArrayList<Maglia> getAnteriore() {
		return anteriore;
	}

	public void setAnteriore(ArrayList<Maglia> anteriore) {
		this.anteriore = anteriore;
	}

	public ArrayList<Maglia> getPosteriore() {
		return posteriore;
	}

	public void setPosteriore(ArrayList<Maglia> posteriore) {
		this.posteriore = posteriore;
	}

	public ArrayList<Maglia> getUnita() {
		return unita;
	}

	public void setUnita(ArrayList<Maglia> unita) {
		this.unita = unita;
	}

	public ArrayList<Maglia> getInglPost() {
		return inglPost;
	}

	public void setInglPost(ArrayList<Maglia> inglPost) {
		this.inglPost = inglPost;
	}

	public ArrayList<Maglia> getInglAnt() {
		return inglAnt;
	}

	public void setInglAnt(ArrayList<Maglia> inglAnt) {
		this.inglAnt = inglAnt;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}
	
	
	

}
