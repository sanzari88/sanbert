package elaborazioneMaglia;

import java.util.ArrayList;

import magliera.puntoMaglia.Maglia;

public class Utility {
	
	public static Maglia[][] capovolgiMatrice(Maglia[][] matriceMaglia) {
		
		// inverto la matrice per lavorare meglio
		int nr = matriceMaglia.length-1;
		int nrNewMax = nr;
		int nc = matriceMaglia[0].length-1;
		int ncNewMax = nc;
		
		Maglia [][] matriceMaglieTmp = new Maglia [nr+1][nc+1];
		
		for(int nrNew =0 ; nr>= 0; nr--) {
				for(int ncNew =0; ncNew<=ncNewMax; ncNew++) {
					matriceMaglieTmp[nrNew][ncNew]=matriceMaglia[nr][ncNew];
				}
				nrNew++;
		}
		return matriceMaglieTmp;
		
	}
	
	
	public static String normalizzaIndiceDisegno(int x) {
		if (x<10)
			return "D000"+x;
		else if(x<100)
			return "D00"+x;
		else if(x<1000)
			return "D0"+x;
		else
			return"D"+x;
	}
	
	public static boolean isMaglieColoreUguali(ArrayList<Maglia> magliaColore) {
		for(int i=0; i< magliaColore.size();i++) {
			if(i!=magliaColore.size()-1 && !magliaColore.get(i).getTipoLavoro().equalsIgnoreCase(magliaColore.get(i+1).getTipoLavoro()))
				return false;
			
		}
		return true;
		}
	
	public static int getNextFreeColor(ArrayList<String> colors) {
		int newcolor=0;
		boolean trovato = false;
		int i= 65;
		for(;i<90 && !trovato; i++) {
			String newcolore= Character.toString((char)i);
			if(!colors.contains(newcolore)) {
				newcolor = i;
				trovato=true;
			}
		}
		
		return newcolor;
	}

}
