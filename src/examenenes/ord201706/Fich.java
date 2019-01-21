package examenenes.ord201706;

import java.util.*;

/** Clase simplificada para gestionar representaciones de los datos b�sicos de un fichero
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Fich {
	protected String pathCompleto;
	protected String nombre;
	protected Date fecha;
	protected long tamanyo; // bytes
	protected long fechaActualizacion;
	/** Constructor de objeto que representa un fichero
	 * @param nombre	Nombre y extensi�n
	 * @param fecha	Fecha de �ltima modificaci�n (milisegundos desde 1/1/1970)
	 * @param tamanyo	Tama�o del fichero (bytes)
	 */
	public Fich( String nombre, Date fecha, long tamanyo ) {
		this.pathCompleto = nombre;
		this.nombre = nombre;
		this.fecha = fecha;
		this.tamanyo = tamanyo;
		this.fechaActualizacion = System.currentTimeMillis();
	}
	/** Constructor de objeto que representa un fichero
	 * @param pathCompleto	Path completo
	 * @param nombre	Nombre y extensi�n
	 * @param fecha	Fecha de �ltima modificaci�n (milisegundos desde 1/1/1970)
	 * @param tamanyo	Tama�o del fichero (bytes)
	 */
	public Fich( String pathCompleto, String nombre, Date fecha, long tamanyo ) {
		this.pathCompleto = pathCompleto;
		this.nombre = nombre;
		this.fecha = fecha;
		this.tamanyo = tamanyo;
		this.fechaActualizacion = System.currentTimeMillis();
	}
	public String getPathCompleto() {
		return pathCompleto;
	}
	public void setPathCompleto(String pathCompleto) {
		this.pathCompleto = pathCompleto;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public long getTamanyo() {
		return tamanyo;
	}
	public void setTamanyo(int tamanyo) {
		this.tamanyo = tamanyo;
	}
	public long getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(long fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	@Override
	public String toString() {
		return "Fich [pathCompleto=" + pathCompleto + ", nombre=" + nombre + ", fecha=" + Utils.sdf.format(fecha) + ", tamanyo=" + Utils.getEspacioDesc(tamanyo)
				+ ", fechaActualizacion=" + Utils.sdf.format(fechaActualizacion) + "]";
	}
	
	
}
