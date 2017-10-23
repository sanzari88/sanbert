package Comandi;

import elaborazioneMaglia.Caduta;
import elaborazioneMaglia.LavoroCaduta;

public class Trasformatore {
	
	public Trasformatore() {}
	
	
	public String vaiLavoro(LavoroCaduta c1,LavoroCaduta c2) {
		String a1 = c1.getAnteriore();
		String a2 = c2.getAnteriore();
		
		String p1 = c1.getPosteriore();
		String p2 = c2.getPosteriore();
		
		int g1=c1.getGuidafilo();
		int g2=c2.getGuidafilo();
		
		StringBuilder comando= new StringBuilder();
		
		if(a1.length()>0)
			comando.append(getComponiLavoro(a1));
		if(p1.length()>0)
			comando.append(" - ").append(getComponiLavoro(p1));
		
		comando.append(" / ");
		
		if(a2.length()>0)
			comando.append(getComponiLavoro(a2));
		if(p2.length()>0)
			comando.append(" - ").append(getComponiLavoro(p2));
		
		comando.append(getComponiComandiGenerici(c1));
		
		comando.append(", GF = ");
		
		if(a1.length()>0)
			comando.append(g1+"A("+a1+p1+")");
		
		if(a2.length()>0)
			comando.append(" / "+g2+"A("+a2+p2+")");
		
		comando.append(", S1 S2;");

		return comando.toString();
	}
	
	private String getComponiComandiGenerici(LavoroCaduta c) {
		
		int g= c.getGradazione();
		int v= c.getVelocita();
		int t=c.getTirapezza();
		
		StringBuilder comando = new StringBuilder();
		comando.append(", CM = GRAD"+g+" TPZ = "+t+" VEL = "+v);
		
		return comando.toString();
	}
	
	private String getComponiLavoro(String l) {
		StringBuilder comando = new StringBuilder();
		comando.append("LA,[] ("+l+")");
		return comando.toString();
	}
	
	

	public String vaiLavoro(LavoroCaduta c) {
		String a1 = c.getAnteriore();
		String p1 = c.getPosteriore();
		int g1=c.getGuidafilo();
		
		StringBuilder comando= new StringBuilder();
		
		if(a1.length()>0)
			comando.append(getComponiLavoro(a1));
		if(p1.length()>0)
			comando.append(" - ").append(getComponiLavoro(p1)).append(" / ");
		
		comando.append(getComponiComandiGenerici(c));
		
		comando.append(", GF = ");
		
		if(a1.length()>0)
			comando.append(g1+"A("+a1+p1+")");
		
		comando.append(", S1;");
		
		return comando.toString();
	}

	public String vaiTrasporto(LavoroCaduta c) {
		String aD = c.getAnteriore();
		String dA = c.getPosteriore();
		
		int v= c.getVelocita();
		int t=c.getTirapezza();
		
		StringBuilder comando = new StringBuilder();
		
		if(aD.length()>0)
			comando.append(" TR,[] ("+aD+") - TR,[] ("+aD+")");
		
		if(dA.length()>0)
			comando.append("/ TR,[] ("+dA+") - TR,[] ("+dA+")");
		
		comando.append(", CM = TPZ = "+t+" VEL = "+v);
		
		comando.append(", S1 S2;");
		
		return comando.toString();
		
	}

}
