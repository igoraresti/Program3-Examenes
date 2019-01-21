package examenenes.parc201805;

import java.util.ArrayList;
import examenenes.parc201805.datos.Liga;
import examenenes.parc201805.iu.VentanaLiga;

/** Clase principal de gesti�n de liga. Lanza la ventana de gesti�n con datos de liga de prueba
 */
public class MainGestionLiga {

	public static void main(String[] args) {
		Liga ligaPrueba = Liga.initEjemplo();
		ArrayList<Liga> ligas = new ArrayList<>();
		ligas.add( ligaPrueba );
		VentanaLiga vent = new VentanaLiga( ligas );
		vent.setVisible( true );
	}

}
