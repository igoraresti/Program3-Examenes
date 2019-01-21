package examenenes.ord201706;

import java.text.SimpleDateFormat;

/** Utilidades para aplicaci�n DiskUseTool
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Utils {
	/** Formateador de fecha dd/mm/aaaa
	 */
	public static final SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
	
	/** Devuelve un espacio de fichero(s) formateado
	 * @param espacioEnBytes	N�mero de bytes utilizados
	 * @return	Maquetaci�n de ese espacio en Kb, Mb o Gb seg�n el tama�o
	 */
	public static String getEspacioDesc( long espacioEnBytes ) {
		long espacio = espacioEnBytes;
		String unidad = "b";
		if (espacio > 1024L*1024L) {
			unidad = "Kb";
			espacio = espacio/1024L;
			if (espacio > 1024L*1024L) {
				unidad = "Mb";
				espacio = espacio/1024L;
				if (espacio > 1024L*1024L) {
					unidad = "Gb";
					espacio = espacio/1024L;
				}
			}
		}
		return String.format( "%1$,d " + unidad, espacio );
	}
}
