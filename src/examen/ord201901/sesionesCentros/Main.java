package examen.ord201901.sesionesCentros;

/** Clase principal de ejecución
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Main {

	/** Método principal
	 * @param args	No utilizados
	 */
	public static void main(String[] args) {
		// Lanzamiento de ventana
		Datos.v = new VentanaDatos();
		Datos.v.setVisible( true );
		// Carga de datos de centros educativos
		Datos.centros = CentroEd.cargaCentros();
		// Visualización de datos de seguimiento
		Tabla c = Tabla.createTablaFromColl( Datos.centros.values() );
		Datos.v.setTabla( c );
	}

}
