package jdraw.gui;

public class MagliaTool extends Tool{
	
	private static String tipoMaglia;
	
	public MagliaTool(String tipoMaglia) {
		
		super.setStrumentoMaglia(true);
		super.setMagliaSelezionata(tipoMaglia);
	}


	public void setTipoMaglia(String tipoMaglia) {
		super.setMagliaSelezionata(tipoMaglia);
	}

	

	
	
	
	
	

}
