package elaborazioneMaglia;

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

}
