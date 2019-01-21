package examen.parc201805;

import java.util.ArrayList;

import examen.parc201805.datos.Liga;
import examen.parc201805.iu.VentanaLiga;

/** Clase principal de gestión de liga. Lanza la ventana de gestión con datos de liga de prueba
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
