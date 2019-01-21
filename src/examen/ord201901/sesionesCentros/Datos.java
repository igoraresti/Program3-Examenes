package examen.ord201901.sesionesCentros;

import java.text.SimpleDateFormat;
import java.util.*;

import examen.ord201901.sesionesCentros.Datos.Sesion;

/** Clase no instanciable para gestionar los datos y los elementos globales del examen
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Datos {
	public static VentanaDatos v;                       // Ventana de la aplicación
	public static HashMap<String,CentroEd> centros;     // Mapa de centros educativos (clave = código de centro)


	// T3
		
	public static SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );  // Formateador de fecha para la tarea 3
	public static class Sesion {  // Clase Datos.Sesion de utilidad para la tarea 3
		int estudiantes;  // Número de estudiantes en la sesión
		long fecha;       // Fecha de la sesión
		public Sesion( int estudiantes, long fecha ) {
			this.estudiantes = estudiantes;
			this.fecha = fecha;
		}
		public int getEstudiantes() {
			return estudiantes;
		}
		public void setEstudiantes(int estudiantes) {
			this.estudiantes = estudiantes;
		}
		public long getFecha() {
			return fecha;
		}
		public void setFecha(long fecha) {
			this.fecha = fecha;
		}
		@Override
		public String toString() {
			return sdf.format( new Date(fecha) ) + " - " + estudiantes;
		}
	}
	
}
