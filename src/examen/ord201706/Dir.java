package examen.ord201706;

import java.util.*;

/** Clase simplificada para gestionar representaciones de los datos básicos de una carpeta
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Dir extends Fich {
	protected ArrayList<Fich> fichs;
	public Dir( String nombre, Date fecha, Fich...fichs ) {
		super( nombre, fecha, 0 );
		this.fichs = new ArrayList<>( Arrays.asList( fichs ));
	}
	@Override
	public String toString() {
		return "Dir [pathCompleto=" + pathCompleto + ", nombre=" + nombre + ", fecha=" + Utils.sdf.format(fecha) + ", tamanyo=" + Utils.getEspacioDesc(tamanyo)
				+ ", fechaActualizacion=" + Utils.sdf.format(fechaActualizacion) + " (" + fichs.size() + " fics)]";
	}
	
}
