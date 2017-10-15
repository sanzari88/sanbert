package Inizio;

import java.io.File;



public class Balza {
	

private File f;
private String nome;
	
	public Balza(String nome, File f) {
		this.nome=nome;
		this.f=f;
	}
	
	public String toString() {
		return nome.replaceAll(".txt", "");
	}
	

}
