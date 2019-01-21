package examen.parc201805.datos;

/** Clase para instancias de equipos deportivos compitiendo en una liga
 */
public class Equipo {
	String codigo;
	String nombre;
	Deporte deporte;
	int puntos;
	
	/** Construye un equipo nuevo asignándole 0 puntos
	 * @param codigo	Código del equipo
	 * @param nombre	Nombre del equipo
	 * @param deporte	Deporte del equipo
	 */
	public Equipo( String codigo, String nombre, Deporte deporte ) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.deporte = deporte;
		puntos = 0;
	}
	
	/** Devuelve el código (único) del equipo
	 * @return	Código del equipo
	 */
	public String getCodigo() {
		return codigo;
	}
	
	/** Devuelve el nombre del equipo
	 * @return	Nombre del equipo
	 */
	public String getNombre() {
		return nombre;
	}
	
	/** Devuelve el deporte
	 * @return	Deporte del equipo
	 */
	public Deporte getDeporte() {
		return deporte;
	}
	
	/** Devuelve los puntos del equipo
	 * @return	Puntos del equipo
	 */
	public int getPuntos() {
		return puntos;
	}
	
	/** Incrementa los puntos del equipo en el valor indicado
	 * @param puntosMas	Número de puntos (0, 1, o 3) de incremento
	 */
	public void incPuntos(int puntosMas) {
		puntos += puntosMas;
	}
	
	/** Pone a cero los puntos del equipo
	 */
	public void resetPuntos() {
		puntos = 0;
	}
	
	@Override
	public String toString() {
		return nombre;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Equipo) {
			return codigo.equals( ((Equipo) obj).getCodigo() );
		} else {
			return false;
		}
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Equipo( codigo, nombre, deporte.duplicaDeporte() );
	}
	
}
