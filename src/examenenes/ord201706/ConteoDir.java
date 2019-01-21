package examenenes.ord201706;

import java.io.File;

/** Clase para registrar estad�sticas de directorios: n�mero de ficheros, n�mero de carpetas y tama�o en bytes
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class ConteoDir {
	File directorio;  // Objeto de referencia del directorio
	int numFicheros;  // N�mero de ficheros
	int numCarpetas;  // N�mero de carpetas
	long tamFicheros; // Tama�o de los ficheros
	/** Crea un contador de directorio nulo a cero
	 */
	public ConteoDir() {
	}
	/** Crea un contador de directorio a cero
	 * @param directorio	Referencia al directorio
	 */
	public ConteoDir(File directorio) {
		this.directorio = directorio;
	}
	/** Crea un contador de directorio con los valores indicados
	 * @param directorio	Referencia al directorio
	 * @param numFicheros	N�mero de ficheros en el directorio
	 * @param numCarpetas	N�mero de carpetas en el directorio
	 * @param tamFicheros	Tama�o total de los ficheros en el directorio (en bytes)
	 */
	public ConteoDir(File directorio, int numFicheros, int numCarpetas, long tamFicheros) {
		super();
		this.directorio = directorio;
		this.numFicheros = numFicheros;
		this.numCarpetas = numCarpetas;
		this.tamFicheros = tamFicheros;
	}
	public File getDirectorio() {
		return directorio;
	}
	public void setDirectorio(File directorio) {
		this.directorio = directorio;
	}
	public int getNumFicheros() {
		return numFicheros;
	}
	public void setNumFicheros(int numFicheros) {
		this.numFicheros = numFicheros;
	}
	public int getNumCarpetas() {
		return numCarpetas;
	}
	public void setNumCarpetas(int numCarpetas) {
		this.numCarpetas = numCarpetas;
	}
	public long getTamFicheros() {
		return tamFicheros;
	}
	public void setTamFicheros(long tamFicheros) {
		this.tamFicheros = tamFicheros;
	}
	/** Suma al contador de directorio otro contador
	 * @param parcial	contador de directorio a sumar a contador en curso (se suman los tres datos: n�mero de ficheros, n�mero de carpetas, tama�o de ficheros)
	 */
	public void sumaConteo( ConteoDir parcial ) {
		numFicheros += parcial.numFicheros;
		numCarpetas += parcial.numCarpetas;
		tamFicheros += parcial.tamFicheros;
	}
	@Override
	public String toString() {
		return "[" + numFicheros + " (" + tamFicheros + "), " + numCarpetas + "]";
	}
	public String toStringConNombre() {
		return "[" + (directorio==null ? "null" : directorio.getAbsolutePath()) + ":" + numFicheros + " (" + tamFicheros + "), " + numCarpetas + "]";
	}
}
