package examenenes.parc201811;

/** Clase principal de ejecuci�n
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Main {

	/** M�todo principal
	 * @param args	No utilizados
	 */
	public static void main(String[] args) {
		// Lanzamiento de ventana
		Datos.v = new VentanaDatos();
		Datos.v.setVisible( true );
		// Carga de datos de centros educativos
		Datos.centros = CentroEd.cargaCentros();
		// Visualizaci�n de datos de seguimiento
		Tabla c = Tabla.createTablaFromColl( Datos.centros.values() );
		Datos.v.setTabla( c );
	}

}
