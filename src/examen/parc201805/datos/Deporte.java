package examen.parc201805.datos;

/** Clase abstracta para representar a cualquier objeto de deporte que se pueda estructurar 
 * en una liga de partidos entre dos equipos
 */
public abstract class Deporte {
	
	protected String nombre;  // Nombre del deporte
	
	/** Devuelve los puntos que se le asignan al equipo 1 (de casa) del partido indicado
	 * @param partido	Partido que se evalúa
	 * @return	Puntos del equipo 1 de ese partido
	 */
	public abstract int getPuntos1( Partido partido );
	
	/** Devuelve los puntos que se le asignan al equipo 2 (visitante) del partido indicado
	 * @param partido	Partido que se evalúa
	 * @return	Puntos del equipo 2 de ese partido
	 */
	public abstract int getPuntos2( Partido partido );
	
	/** Actualiza los valores que tenga el equipo indicado en el partido indicado al objeto deporte en curso
	 * @param equipo	Partido asociado a este deporte
	 * @param equipo	Equipo asociado a este deporte (debe ser uno de los equipos del partido)
	 */
	public abstract void actualiza( Partido partido, Equipo equipo );
	
	/** Inicializa los valores que tenga el deporte en curso
	 */
	public abstract void reset();
	
	/** Compara dos equipos de acuerdo a los criterios de clasificación en liga de ambos, de acuerdo a este deporte
	 * @param equipo1	Datos del equipo 1
	 * @param equipo2	Datos del equipo 2
	 * @return	Negativo si el equipo 1 está MEJOR clasificado, positivo si está PEOR, cero si están EMPATADOS
	 */
	public abstract int comparaEquipos( Equipo equipo1, Equipo equipo2 );
	
	/** Crea un objeto deporte nuevo inicializado desde cero, del mismo tipo que el deporte actual
	 * @return	deporte del mismo tipo nuevo
	 */
	public abstract Deporte duplicaDeporte();
	
}
