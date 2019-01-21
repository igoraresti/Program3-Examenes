package examen.parc201805.datos;

/** Clase modelo para partidos: enfrentamientos entre dos equipos, el 1 (de "casa") y el 2 ("visitante")
 */
public class Partido {
	int jornada;
	Equipo equipo1;
	Equipo equipo2;
	int marcador1;
	int marcador2;
	
	/** Crea un nuevo partido
	 * @param equipo1	Equipo 1 (de casa)
	 * @param equipo2	Equipo 2 (visitante)
	 * @param marcador1	Marcador del equipo 1
	 * @param marcador2	Marcador del equipo 2
	 * @throws NullPointerException	Si alguno de los dos equipos es null
	 */
	public Partido( int jornada, Equipo equipo1, Equipo equipo2, int marcador1, int marcador2 ) throws NullPointerException {
		if (equipo1==null || equipo2==null) throw new NullPointerException( "Intento de creación de partido de equipo nulo" );
		this.jornada = jornada;
		this.equipo1 = equipo1;
		this.equipo2 = equipo2;
		this.marcador1 = marcador1;
		this.marcador2 = marcador2;
	}
	
	/** Devuelve la jornada del partido
	 * @return	Número de jornada (empezando en 1)
	 */
	public int getJornada() {
		return jornada;
	}

	/** Cambia la jornada del partido
	 * @param jornada	Número de jornada (empezando en 1)
	 */
	public void setJornada(int jornada) {
		this.jornada = jornada;
	}

	/** Devuelve el equipo 1 (de casa)
	 * @return	equipo de casa
	 */
	public Equipo getEquipo1() {
		return equipo1;
	}
	/** Modifica el equipo 1
	 * @param equipo1	Equipo de casa
	 */
	public void setEquipo1(Equipo equipo1) {
		this.equipo1 = equipo1;
	}
	/** Devuelve el equipo 2 (visitante)
	 * @return	equipo visitante
	 */
	public Equipo getEquipo2() {
		return equipo2;
	}
	/** Modifica el equipo 2
	 * @param equipo2	Equipo visitante
	 */
	public void setEquipo2(Equipo equipo2) {
		this.equipo2 = equipo2;
	}
	/** Devuelve el marcador del equipo 1 (de casa)
	 * @return	marcador del equipo de casa
	 */
	public int getMarcador1() {
		return marcador1;
	}
	/** Modifica el marcador del equipo 1
	 * @param marcador1	Marcador del equipo de casa
	 */
	public void setMarcador1(int marcador1) {
		this.marcador1 = marcador1;
	}
	/** Devuelve el marcador del equipo 2 (visitante)
	 * @return	marcador del equipo visitante
	 */
	public int getMarcador2() {
		return marcador2;
	}
	/** Modifica el marcador del equipo 2
	 * @param marcador2	Marcador del equipo visitante
	 */
	public void setMarcador2(int marcador2) {
		this.marcador2 = marcador2;
	}

	@Override
	public String toString() {
		return equipo1 + " - " + equipo2 + " " + marcador1 + "-" + marcador2;
	}
}
